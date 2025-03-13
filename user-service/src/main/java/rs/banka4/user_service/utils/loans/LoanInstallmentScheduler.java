package rs.banka4.user_service.utils.loans;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import rs.banka4.user_service.config.RabbitMqConfig;
import rs.banka4.user_service.domain.auth.dtos.NotificationTransferDto;
import rs.banka4.user_service.domain.loan.db.Loan;
import rs.banka4.user_service.domain.loan.db.LoanInstallment;
import rs.banka4.user_service.domain.loan.db.LoanStatus;
import rs.banka4.user_service.domain.loan.db.PaymentStatus;
import rs.banka4.user_service.domain.account.db.Account;
import rs.banka4.user_service.repositories.AccountRepository;
import rs.banka4.user_service.repositories.LoanInstallmentRepository;
import rs.banka4.user_service.repositories.LoanRepository;
import rs.banka4.user_service.runners.TestDataRunner;
import rs.banka4.user_service.utils.MessageHelper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanInstallmentScheduler {
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final RabbitTemplate rabbitTemplate;
    private final LoanRateUtil loanRateUtil;
    private final ApplicationContext applicationContext;

    private static final BigDecimal LATE_PAYMENT_PENALTY = new BigDecimal("0.05");
    private static final BigDecimal LEGAL_THRESHOLD = new BigDecimal("1000");

    /**
     * Daily job at 1 AM: Process due installments.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDueInstallments() {
        LoanInstallmentScheduler self = applicationContext.getBean(LoanInstallmentScheduler.class);
        List<LoanInstallment> dueInstallments = loanInstallmentRepository.findByExpectedDueDateAndPaymentStatus(LocalDate.now(), PaymentStatus.UNPAID);

        for (LoanInstallment installment : dueInstallments) {
            self.payInstallmentIfPossible(installment); // **Calls proxied method**
        }
    }

    /**
     * Retries `DELAYED` installments every 6 hours for up to 72 hours.
     */
    @Scheduled(cron = "0 0 */6 * * ?")
    public void retryDelayedInstallments() {
        LoanInstallmentScheduler self = applicationContext.getBean(LoanInstallmentScheduler.class);
        LocalDate overdueThreshold = LocalDate.now().minusDays(3);
        List<LoanInstallment> delayedInstallments = loanInstallmentRepository.findRecentDelayedInstallments(PaymentStatus.DELAYED, overdueThreshold);

        for (LoanInstallment installment : delayedInstallments) {
            self.payInstallmentIfPossible(installment); // **Calls proxied method**
        }
    }

    /**
     * Daily at midnight: Apply penalties for `DELAYED` installments after 72 hours.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void applyLatePaymentPenalties() {
        LoanInstallmentScheduler self = applicationContext.getBean(LoanInstallmentScheduler.class);
        LocalDate threshold = LocalDate.now().minusDays(3);
        List<LoanInstallment> delayedInstallments = loanInstallmentRepository.findByPaymentStatusAndExpectedDueDate(PaymentStatus.DELAYED, threshold);

        for (LoanInstallment installment : delayedInstallments) {
            self.applyPenaltyToInstallment(installment); // **Calls proxied method**
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void applyPenaltyToInstallment(LoanInstallment installment) {
        Loan loan = installment.getLoan();
        // Apply penalty
        installment.setInterestRateAmount(installment.getInterestRateAmount().add(LATE_PAYMENT_PENALTY));
        loan.setBaseInterestRate(loan.getBaseInterestRate().add(LATE_PAYMENT_PENALTY));

        loanRepository.save(loan);
        loanInstallmentRepository.save(installment);

        // Send notification for applied penalty
        NotificationTransferDto message = MessageHelper.createLoanInstallmentPenaltyMessage(
                loan.getAccount().getClient().email,
                loan.getAccount().getClient().firstName,
                loan.getLoanNumber(),
                LATE_PAYMENT_PENALTY,
                LocalDate.now()
        );

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_KEY,
                message
        );

        // Check if total overdue exceeds legal threshold
        if (loan.getRemainingDebt().compareTo(LEGAL_THRESHOLD) > 0) {
            // TODO: Implement logic for handling legal threshold breach
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void payInstallmentIfPossible(LoanInstallment installment){
        Loan loan = installment.getLoan();
        Account account = loan.getAccount();

        BigDecimal installmentAmount = installment.getInstallmentAmount();
        if (account.getAvailableBalance().compareTo(installmentAmount) >= 0) {

            // Subtract from user account and paying installment
            account.setBalance(account.getBalance().subtract(installmentAmount));
            account.setAvailableBalance(account.getAvailableBalance().subtract(installmentAmount));
            accountRepository.save(account);
            loan.setRemainingDebt(loan.getRemainingDebt().subtract(installmentAmount));

            // In case loan is paid off.
            if (loan.getRemainingDebt().compareTo(BigDecimal.ZERO) <= 0){
                loan.setStatus(LoanStatus.PAID_OFF);
                loan.setNextInstallmentDate(null);
            }
            else {
                // Creating next installment
                LoanInstallment newInstallment = new LoanInstallment();
                newInstallment.setLoan(loan);
                newInstallment.setCurrency(installment.getCurrency());
                newInstallment.setExpectedDueDate(installment.getExpectedDueDate().plusMonths(1));
                newInstallment.setActualDueDate(null);
                newInstallment.setPaymentStatus(PaymentStatus.UNPAID);

                // Loan interest type is fixed
                if (loan.getInterestType() == Loan.InterestType.FIXED) {
                    newInstallment.setInstallmentAmount(loan.getMonthlyInstallment());
                    newInstallment.setInterestRateAmount(loan.getBaseInterestRate());
                }
                else { // Loan interest type is variable, calculating in interest rate using base interest rate of loan and adding variance.
                    BigDecimal interestRate = loanRateUtil.calculateInterestRate(
                            loan.getBaseInterestRate().add(LoanRateScheduler.getInterestRateVariant()),
                            loan.getType()
                    );

                    newInstallment.setInterestRateAmount(interestRate);
                    newInstallment.setInstallmentAmount(
                            loanRateUtil.calculateMonthly(
                                    loan.getAmount(),
                                    interestRate,
                                    BigInteger.valueOf(loan.getRepaymentPeriod())
                            )
                    );

                    loan.setNextInstallmentDate(newInstallment.getExpectedDueDate());
                }

                loanInstallmentRepository.save(newInstallment);
            }

            installment.setPaymentStatus(PaymentStatus.PAID);
            installment.setActualDueDate(LocalDate.now());
            loanInstallmentRepository.save(installment);
            loanRepository.save(loan);

            // Message for successful payment
            NotificationTransferDto message = MessageHelper.createLoanInstallmentPaidMessage(
                    account.getClient().email,
                    account.getClient().firstName,
                    loan.getLoanNumber(),
                    installment.getInstallmentAmount(),
                    account.getCurrency().getCode(),
                    LocalDate.now()
            );
            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.EXCHANGE_NAME,
                    RabbitMqConfig.ROUTING_KEY,
                    message
            );
        } else {
            // Mark as delayed
            installment.setPaymentStatus(PaymentStatus.DELAYED);
            loanInstallmentRepository.save(installment);

            // Message for denied payment.
            NotificationTransferDto message = MessageHelper.createLoanInstallmentPaymentDeniedMessage(
                    account.getClient().email,
                    account.getClient().firstName,
                    loan.getLoanNumber(),
                    installment.getInstallmentAmount(),
                    account.getCurrency().getCode(),
                    LocalDate.now()
            );
            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.EXCHANGE_NAME,
                    RabbitMqConfig.ROUTING_KEY,
                    message
            );
        }
    }
}