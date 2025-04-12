package rs.banka4.rafeisen.common.dto;

import rs.banka4.rafeisen.common.currency.CurrencyCode;

public record AccountNumberDto(
    String accountNumber,
    CurrencyCode currency
) {
}
