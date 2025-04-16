package rs.banka4.bank_service.domain.security.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.listing.dtos.SecurityType;

import java.time.OffsetDateTime;


public record SecurityHoldingDto(
    @Schema(description = "Ticker symbol of the security.", example = "AAPL")
    String ticker,

    @Schema(description = "Total number of units owned.", example = "30")
    int amount,

    @Schema(description = "Current market price per unit.", example = "172.50")
    MonetaryAmount price,

    @Schema(description = "Unrealized profit (or loss) for the holding.", example = "495.00")
    MonetaryAmount profit,
    @Schema(description = "Public number of units owned.", example = "30")
    int publicAmount,
    @Schema(description = "Timestamp of the last transaction (buy/sell) affecting this holding.", example = "2025-03-28T14:12:00+00:00")
    OffsetDateTime lastModified

) {
}
