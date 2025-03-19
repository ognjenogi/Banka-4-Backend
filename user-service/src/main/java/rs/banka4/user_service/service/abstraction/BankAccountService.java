package rs.banka4.user_service.service.abstraction;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import rs.banka4.user_service.domain.account.db.Account;
import rs.banka4.user_service.domain.currency.db.Currency;
import rs.banka4.user_service.domain.transaction.dtos.TransactionDto;

public interface BankAccountService {
    List<Account> getBankAccounts();

    Account getBankAccountForCurrency(Currency.Code currency);

    Page<TransactionDto> getAllTransactionsForBank(
        Authentication authentication,
        PageRequest pageRequest
    );
}
