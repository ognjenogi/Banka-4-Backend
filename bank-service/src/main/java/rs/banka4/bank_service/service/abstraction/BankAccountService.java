package rs.banka4.bank_service.service.abstraction;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import rs.banka4.bank_service.domain.account.db.Account;
import rs.banka4.bank_service.domain.account.dtos.BankAccountDto;
import rs.banka4.bank_service.domain.transaction.dtos.TransactionDto;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

public interface BankAccountService {
    List<Account> getBankAccounts();

    Account getBankAccountForCurrency(CurrencyCode currency);

    Page<TransactionDto> getAllTransactionsForBank(
        Authentication authentication,
        PageRequest pageRequest
    );

    List<BankAccountDto> getAllBankAccountWithCurrency();
}
