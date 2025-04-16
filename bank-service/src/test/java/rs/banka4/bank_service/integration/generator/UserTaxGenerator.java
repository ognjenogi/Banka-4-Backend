package rs.banka4.bank_service.integration.generator;

import java.math.BigDecimal;
import java.util.UUID;
import rs.banka4.bank_service.domain.account.db.Account;
import rs.banka4.bank_service.domain.taxes.db.UserTaxDebts;
import rs.banka4.bank_service.domain.user.client.db.Client;
import rs.banka4.bank_service.generator.AccountObjectMother;
import rs.banka4.bank_service.repositories.AccountRepository;
import rs.banka4.bank_service.repositories.UserRepository;
import rs.banka4.bank_service.repositories.UserTaxDebtsRepository;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

public class UserTaxGenerator {

    public static void createDummyTax(
        Client client,
        UserRepository userRepo,
        AccountRepository accountRepo,
        UserTaxDebtsRepository debtRepo
    ) {
        var account = AccountObjectMother.generateBasicToAccount();
        account.setClient(client);
        userRepo.save(account.getEmployee());
        accountRepo.save(account);
        var dept =
            UserTaxDebts.builder()
                .debtAmount(BigDecimal.valueOf(100))
                .yearlyDebtAmount(BigDecimal.valueOf(1000))
                .account(account)
                .build();
        debtRepo.save(dept);
    }

    public static BigDecimal createDummyTaxEur(
        Client client,
        UserRepository userRepo,
        AccountRepository accountRepo,
        UserTaxDebtsRepository debtRepo
    ) {
        var account = AccountObjectMother.generateBasicEURFromAccount();
        account.setClient(client);
        userRepo.save(account.getEmployee());
        accountRepo.save(account);
        var dept =
            UserTaxDebts.builder()
                .debtAmount(BigDecimal.valueOf(50))
                .yearlyDebtAmount(BigDecimal.valueOf(200))
                .account(account)
                .build();
        debtRepo.save(dept);
        return BigDecimal.valueOf(50);
    }

    public static BigDecimal createMultipleDebts(
        Client client,
        int count,
        UserRepository userRepo,
        AccountRepository accountRepo,
        UserTaxDebtsRepository debtRepo
    ) {
        BigDecimal debtTotal = BigDecimal.ZERO;
        for (int i = 0; i < count; i++) {
            Account account =
                (i % 2 == 0)
                    ? AccountObjectMother.generateBasicToAccount()
                    : AccountObjectMother.generateBasicEURFromAccount();
            account.setAccountNumber(
                UUID.randomUUID()
                    .toString()
            );
            account.setClient(client);
            userRepo.save(account.getEmployee());
            accountRepo.save(account);

            BigDecimal debtAmount = BigDecimal.valueOf((i + 1) * 100);
            BigDecimal yearlyDebtAmount = BigDecimal.valueOf((i + 1) * 1000);

            if (account.getCurrency() != CurrencyCode.RSD) {
                debtAmount = debtAmount.multiply(BigDecimal.valueOf(0.5)); // e.g. half
                yearlyDebtAmount = yearlyDebtAmount.multiply(BigDecimal.valueOf(0.2));
            }
            debtTotal = debtTotal.add(debtAmount);
            UserTaxDebts debt =
                UserTaxDebts.builder()
                    .account(account)
                    .debtAmount(debtAmount)
                    .yearlyDebtAmount(yearlyDebtAmount)
                    .build();

            debtRepo.save(debt);
        }
        return debtTotal;
    }

    public static BigDecimal createMultipleDebtsEUR(
        Client client,
        int count,
        UserRepository userRepo,
        AccountRepository accountRepo,
        UserTaxDebtsRepository debtRepo
    ) {
        BigDecimal debtTotal = BigDecimal.ZERO;
        for (int i = 0; i < count; i++) {
            Account account = AccountObjectMother.generateBasicEURFromAccount();
            account.setAccountNumber(
                UUID.randomUUID()
                    .toString()
            );
            account.setClient(client);
            userRepo.save(account.getEmployee());
            accountRepo.save(account);

            BigDecimal debtAmount = BigDecimal.valueOf((i + 1) * 100);
            BigDecimal yearlyDebtAmount = BigDecimal.valueOf((i + 1) * 1000);

            if (account.getCurrency() != CurrencyCode.RSD) {
                debtAmount = debtAmount.multiply(BigDecimal.valueOf(0.5)); // e.g. half
                yearlyDebtAmount = yearlyDebtAmount.multiply(BigDecimal.valueOf(0.2));
            }
            debtTotal = debtTotal.add(debtAmount);
            UserTaxDebts debt =
                UserTaxDebts.builder()
                    .account(account)
                    .debtAmount(debtAmount)
                    .yearlyDebtAmount(yearlyDebtAmount)
                    .build();

            debtRepo.save(debt);
        }
        return debtTotal;
    }

    public static BigDecimal createMultipleDebtsRSD(
        Client client,
        int count,
        UserRepository userRepo,
        AccountRepository accountRepo,
        UserTaxDebtsRepository debtRepo
    ) {
        BigDecimal debtTotal = BigDecimal.ZERO;
        for (int i = 0; i < count; i++) {
            Account account = AccountObjectMother.generateBasicToAccount();
            account.setAccountNumber(
                UUID.randomUUID()
                    .toString()
            );
            account.setClient(client);
            userRepo.save(account.getEmployee());
            accountRepo.save(account);

            BigDecimal debtAmount = BigDecimal.valueOf((i + 1) * 100);
            BigDecimal yearlyDebtAmount = BigDecimal.valueOf((i + 1) * 1000);

            if (account.getCurrency() != CurrencyCode.RSD) {
                debtAmount = debtAmount.multiply(BigDecimal.valueOf(0.5)); // e.g. half
                yearlyDebtAmount = yearlyDebtAmount.multiply(BigDecimal.valueOf(0.2));
            }
            debtTotal = debtTotal.add(debtAmount);
            UserTaxDebts debt =
                UserTaxDebts.builder()
                    .account(account)
                    .debtAmount(debtAmount)
                    .yearlyDebtAmount(yearlyDebtAmount)
                    .build();

            debtRepo.save(debt);
        }
        return debtTotal;
    }

    public static void createMultipleDebtsInsufficientFunds(
        Client client,
        int count,
        UserRepository userRepo,
        AccountRepository accountRepo,
        UserTaxDebtsRepository debtRepo
    ) {
        for (int i = 0; i < count; i++) {
            Account account =
                (i % 2 == 0)
                    ? AccountObjectMother.generateBasicToAccount()
                    : AccountObjectMother.generateBasicEURFromAccount();
            account.setAccountNumber(
                UUID.randomUUID()
                    .toString()
            );
            account.setClient(client);
            account.setAvailableBalance(BigDecimal.ZERO);
            userRepo.save(account.getEmployee());
            accountRepo.save(account);

            BigDecimal debtAmount = BigDecimal.valueOf((i + 1) * 100);
            BigDecimal yearlyDebtAmount = BigDecimal.valueOf((i + 1) * 1000);

            if (account.getCurrency() != CurrencyCode.RSD) {
                debtAmount = debtAmount.multiply(BigDecimal.valueOf(0.5));
                yearlyDebtAmount = yearlyDebtAmount.multiply(BigDecimal.valueOf(0.2));
            }

            UserTaxDebts debt =
                UserTaxDebts.builder()
                    .account(account)
                    .debtAmount(debtAmount)
                    .yearlyDebtAmount(yearlyDebtAmount)
                    .build();

            debtRepo.save(debt);
        }
    }
}
