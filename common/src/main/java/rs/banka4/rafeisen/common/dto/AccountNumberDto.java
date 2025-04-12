package rs.banka4.rafeisen.common.dto;

import rs.banka4.rafeisen.common.currency.CurrencyCode;

import java.math.BigDecimal;

public record AccountNumberDto(
    String accountNumber,
    CurrencyCode currency,
    BigDecimal availableBalance
) {
}
