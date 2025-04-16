package rs.banka4.bank_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rs.banka4.bank_service.domain.taxes.db.dto.TaxableUserDto;
import rs.banka4.bank_service.exceptions.transaction.InsufficientFunds;
import rs.banka4.bank_service.repositories.AccountRepository;
import rs.banka4.bank_service.repositories.UserTaxDebtsRepository;
import rs.banka4.bank_service.runners.TestDataRunner;
import rs.banka4.bank_service.service.abstraction.ExchangeRateService;
import rs.banka4.bank_service.service.abstraction.TaxService;
import rs.banka4.bank_service.utils.tax.TaxCalculationUtill;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class TaxServiceImp implements TaxService {
    private final UserTaxDebtsRepository userTaxDebtsRepository;
    private final ExchangeRateService exchangeRateService;
    private final TaxCalculationUtill taxCalculationUtill;
    private final AccountRepository accountRepository;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TaxServiceImp.class);

    @Override
    public Page<TaxableUserDto> getTaxSummary(String firstName, String lastName, PageRequest of) {
        return userTaxDebtsRepository.findClientsWithDebt(firstName,lastName,of).map(user ->{
            var debts = userTaxDebtsRepository.findByAccount_Client_Id(user.getId());
            var debtTotal = TaxCalculationUtill.calculateTax(debts, exchangeRateService);
            return new TaxableUserDto(user.getId(),user.getFirstName(),user.getEmail(),user.getLastName(),debtTotal.unpaidTaxThisMonth(),debtTotal.currency());
        });
    }

    @Override
    public void taxUser(UUID userId) {
        var debts = userTaxDebtsRepository.findByAccount_Client_Id(userId);
        var stateAcc = accountRepository.findAccountByAccountNumber(TestDataRunner.STATE_ACCOUNT_NUMBER).orElseThrow(()->new IllegalArgumentException("State account not found"));
        debts.forEach(debt -> taxCalculationUtill.chargeTax(debt.getAccount(),stateAcc));
    }
    @Scheduled(cron = "0 0 0 1 * *")
    @Override
    public void taxMonthly() {
        var stateAcc = accountRepository.findAccountByAccountNumber(TestDataRunner.STATE_ACCOUNT_NUMBER).orElseThrow(()->new IllegalArgumentException("State account not found"));
        userTaxDebtsRepository.findAll().forEach(debt -> {
            try {
                taxCalculationUtill.chargeTax(debt.getAccount(), stateAcc);
            }catch (InsufficientFunds e){
                logger.error("Insufficient funds in account " + debt.getAccount());
            }
        });
    }
    @Scheduled(cron = "0 0 0 1 1 *")
    public void cleanYearlyDebt(){
        userTaxDebtsRepository.findAll().forEach(debt->{
            debt.setYearlyDebtAmount(BigDecimal.ZERO);
            userTaxDebtsRepository.save(debt);
        });
    }
}
