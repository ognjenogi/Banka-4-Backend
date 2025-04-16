package rs.banka4.bank_service.utils.tax;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rs.banka4.bank_service.domain.account.db.Account;
import rs.banka4.bank_service.domain.taxes.db.UserTaxDebts;
import rs.banka4.bank_service.domain.taxes.db.dto.UserTaxInfoDto;
import rs.banka4.bank_service.domain.transaction.db.MonetaryAmount;
import rs.banka4.bank_service.domain.transaction.db.Transaction;
import rs.banka4.bank_service.domain.transaction.db.TransactionStatus;
import rs.banka4.bank_service.exceptions.transaction.InsufficientFunds;
import rs.banka4.bank_service.repositories.AccountRepository;
import rs.banka4.bank_service.repositories.TransactionRepository;
import rs.banka4.bank_service.repositories.UserTaxDebtsRepository;
import rs.banka4.bank_service.service.abstraction.ExchangeRateService;
import rs.banka4.bank_service.service.abstraction.TransactionService;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

@Component
@RequiredArgsConstructor
public class TaxCalculationUtill {
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final ExchangeRateService exchangeRateService;
    private final UserTaxDebtsRepository userTaxDebtsRepository;
    private final AccountRepository accountRepository;

    /**
     * Aggregates a list of {@link UserTaxDebts} into a single {@link UserTaxInfoDto}.
     * <p>
     * Converts any debt amounts not in RSD into RSD using the provided {@link ExchangeRateService},
     * sums up all unpaid and yearly debts, and returns them in a DTO.
     *
     * @param debts the list of debt entries for a single user
     * @param exchangeRateService the service to use for currency conversion
     * @return a {@link UserTaxInfoDto} containing:
     *         <ul>
     *         <li><b>yearlyDebtAmount</b>: total of {@link UserTaxDebts#getYearlyDebtAmount()}</li>
     *         <li><b>unpaidTaxThisMonth</b>: sum of {@link UserTaxDebts#getDebtAmount()}, all
     *         converted to RSD</li>
     *         <li><b>currency</b>: always {@link CurrencyCode#RSD}</li>
     *         </ul>
     */
    public static UserTaxInfoDto calculateTax(
        List<UserTaxDebts> debts,
        ExchangeRateService exchangeRateService
    ) {
        var totalUnpaid =
            debts.stream()
                .map(debt -> {
                    CurrencyCode accountCurrency =
                        debt.getAccount()
                            .getCurrency();
                    if (!accountCurrency.equals(CurrencyCode.RSD)) {
                        return exchangeRateService.convertCurrency(
                            debt.getDebtAmount(),
                            accountCurrency,
                            CurrencyCode.RSD
                        );
                    }
                    return debt.getDebtAmount();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalYearly =
            debts.stream()
                .map(UserTaxDebts::getYearlyDebtAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new UserTaxInfoDto(totalYearly, totalUnpaid, CurrencyCode.RSD);
    }

    /**
     * Performs a tax payment by debiting the user's account and crediting the state account.
     * <p>
     * Looks up the user's outstanding debt in {@link UserTaxDebts}, ensures sufficient funds,
     * performs the currency conversion if needed, updates both account balances, zeroes out the
     * user's debt, and logs a {@link Transaction}.
     *
     * @param accountFrom the user's account from which the tax will be deducted; must match an
     *        existing {@link UserTaxDebts#getAccount()}
     * @param accountTo the state account to which the tax (in RSD) will be credited
     * @throws InsufficientFunds if the user's available balance is less than the debt amount
     * @throws IllegalArgumentException if no debt entry exists for {@code accountFrom}
     */
    @Transactional
    public void chargeTax(Account accountFrom, Account accountTo) {
        /// This will never throw because we call this function for acc from UserTaxDebt table
        UserTaxDebts debt =
            userTaxDebtsRepository.findByAccount_AccountNumber(accountFrom.getAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Account has no tax debt"));

        var rawDebt = debt.getDebtAmount();
        var amountToDebit = rawDebt;

        if (
            accountFrom.getAvailableBalance()
                .compareTo(rawDebt)
                < 0
        ) {
            throw new InsufficientFunds();
        }

        if (
            !accountFrom.getCurrency()
                .equals(accountTo.getCurrency())
        ) {
            amountToDebit =
                exchangeRateService.convertCurrency(
                    rawDebt,
                    accountFrom.getCurrency(),
                    accountTo.getCurrency()
                );
        }

        accountFrom.setAvailableBalance(
            accountFrom.getAvailableBalance()
                .subtract(rawDebt)
        );
        accountFrom.setBalance(
            accountFrom.getBalance()
                .subtract(rawDebt)
        );

        accountTo.setAvailableBalance(
            accountTo.getAvailableBalance()
                .add(amountToDebit)
        );
        accountTo.setBalance(
            accountTo.getBalance()
                .add(amountToDebit)
        );

        debt.setDebtAmount(BigDecimal.ZERO);
        debt.setYearlyDebtAmount(
            debt.getYearlyDebtAmount()
                .add(amountToDebit)
        );
        userTaxDebtsRepository.save(debt);

        accountRepository.saveAll(List.of(accountFrom, accountTo));
        transactionRepository.save(buildTransfer(accountFrom, accountTo, rawDebt, amountToDebit));
    }

    /**
     * Builds a transfer transaction between two accounts.
     *
     * @param fromAccount the source account
     * @param toAccount the destination account
     * @param fromAmount the amount debited from the source (in source currency)
     * @param toAmount the amount credited to the destination (in destination currency)
     * @return a new {@link Transaction} entity ready for persistence
     */
    private Transaction buildTransfer(
        Account fromAccount,
        Account toAccount,
        BigDecimal fromAmount,
        BigDecimal toAmount

    ) {
        return Transaction.builder()
            .transactionNumber(
                UUID.randomUUID()
                    .toString()
            )
            .fromAccount(fromAccount)
            .toAccount(toAccount)
            .from(new MonetaryAmount(fromAmount, fromAccount.getCurrency()))
            .to(new MonetaryAmount(toAmount, toAccount.getCurrency()))
            .recipient(toAccount.getClient().firstName)
            .paymentCode("101")
            .fee(new MonetaryAmount(BigDecimal.ZERO, toAccount.getCurrency()))
            .referenceNumber(
                String.valueOf(
                    toAccount.getClient()
                        .getId()
                )
            )
            .paymentPurpose("Internal")
            .paymentDateTime(LocalDateTime.now())
            .status(TransactionStatus.REALIZED)
            .build();
    }
}
