package rs.banka4.rafeisen.common.dto;

import java.math.BigDecimal;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

public record AccountNumberDto(
    String accountNumber,
    CurrencyCode currency,
    BigDecimal availableBalance
) {
}
