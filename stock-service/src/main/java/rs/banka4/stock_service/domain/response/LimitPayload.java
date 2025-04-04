package rs.banka4.stock_service.domain.response;

import rs.banka4.stock_service.domain.security.forex.db.CurrencyCode;

import java.math.BigDecimal;

public record LimitPayload(
    BigDecimal limitAmount,
    CurrencyCode limitCurrencyCode
) {
}
