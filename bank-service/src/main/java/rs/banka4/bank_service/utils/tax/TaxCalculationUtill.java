package rs.banka4.bank_service.utils.tax;

import rs.banka4.bank_service.domain.taxes.db.UserTaxDebts;
import rs.banka4.bank_service.domain.taxes.db.dto.UserTaxInfoDto;
import rs.banka4.bank_service.service.abstraction.ExchangeRateService;
import rs.banka4.bank_service.service.abstraction.ExchangeService;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

import java.math.BigDecimal;
import java.util.List;

public class TaxCalculationUtill {

    public static UserTaxInfoDto calculateTax(List<UserTaxDebts> debts, ExchangeRateService exchangeRateService) {
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
}
