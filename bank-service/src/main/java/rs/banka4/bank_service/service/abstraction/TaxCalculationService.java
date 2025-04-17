package rs.banka4.bank_service.service.abstraction;

import java.util.List;
import rs.banka4.bank_service.domain.account.db.Account;
import rs.banka4.bank_service.domain.taxes.db.UserTaxDebts;
import rs.banka4.bank_service.domain.taxes.db.dto.UserTaxInfoDto;
import rs.banka4.bank_service.domain.transaction.db.Transaction;
import rs.banka4.bank_service.exceptions.transaction.InsufficientFunds;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

public interface TaxCalculationService {
    /**
     * Aggregates a list of {@link UserTaxDebts} into a single {@link UserTaxInfoDto}.
     * <p>
     * Converts any debt amounts not in RSD into RSD using the provided {@link ExchangeRateService},
     * sums up all unpaid and yearly debts, and returns them in a DTO.
     *
     * @param debts the list of debt entries for a single user
     * @return a {@link UserTaxInfoDto} containing:
     *         <ul>
     *         <li><b>yearlyDebtAmount</b>: total of {@link UserTaxDebts#getYearlyDebtAmount()}</li>
     *         <li><b>unpaidTaxThisMonth</b>: sum of {@link UserTaxDebts#getDebtAmount()}, all
     *         converted to RSD</li>
     *         <li><b>currency</b>: always {@link CurrencyCode#RSD}</li>
     *         </ul>
     */
    UserTaxInfoDto calculateTax(List<UserTaxDebts> debts);

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
    void chargeTax(Account accountFrom, Account accountTo);
}
