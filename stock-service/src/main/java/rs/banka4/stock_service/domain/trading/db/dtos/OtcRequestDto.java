package rs.banka4.stock_service.domain.trading.db.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import rs.banka4.stock_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.stock_service.domain.security.stock.dtos.StockInfoDto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Data Transfer Object representing an OTC request (negotiation) for trading securities")
public record OtcRequestDto(

    @Schema(
        description = "Detailed information about the security (stock) involved in the negotiation",
        implementation = StockInfoDto.class
    )
    StockInfoDto stock,

    @Schema(
        description = "The price per individual security unit",
        example = "150.00"
    )
    MonetaryAmount pricePerStock,

    @Schema(
        description = "The premium amount for the optional contract (opcionog ugovora)",
        example = "400.00"
    )
    MonetaryAmount premium,

    @Schema(
        description = "The amount (number) of securities being negotiated",
        example = "10"
    )
    int amount,

    @Schema(
        description = "Username of the user who initiated (made) the negotiation",
        example = "test"
    )
    String madeBy,

    @Schema(
        description = "Username of the user for whom the negotiation is intended",
        example = "test"
    )
    String madeFor,

    @Schema(
        description = "Username of the user who last modified the negotiation; " +
            "if different from the current user, it implies the negotiation is unread",
        example = "test"
    )
    String modifiedBy,

    @Schema(
        description = "Timestamp of the last modification of the negotiation",
        example = "2024-04-10T12:34:56Z"
    )
    OffsetDateTime lastModifiedDate,

    @Schema(
        description = "The settlement date for the OTC request which determines the expiry date of the optional contract",
        example = "2025-05-22"
    )
    LocalDate settlementDate

) {
}
