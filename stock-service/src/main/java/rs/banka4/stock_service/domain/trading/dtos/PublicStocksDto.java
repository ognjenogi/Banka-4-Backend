package rs.banka4.stock_service.domain.trading.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

import rs.banka4.stock_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.stock_service.domain.listing.dtos.SecurityType;

public record PublicStocksDto(
    SecurityType securityType,
    UUID sellerId,
    UUID stockId,
    String ownerUsername,
    String ticker,
    String name,
    int amount,
    MonetaryAmount activePrice,
    OffsetDateTime lastUpdated
) {
}
