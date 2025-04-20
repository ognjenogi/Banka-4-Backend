package rs.banka4.bank_service.domain.trading.db.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;

@Schema(
    description = "Data Transfer Object representing an OTC request (negotiation) for trading securities"
)
public record OtcRequestUpdateDto(
    @Schema(
        description = "The price per individual security unit",
        example = "150.00"
    ) MonetaryAmount pricePerStock,

    @Schema(
        description = "The premium amount for the optional contract (opcionog ugovora)",
        example = "400.00"
    ) MonetaryAmount premium,

    @Schema(
        description = "The amount (number) of securities being negotiated",
        example = "10"
    ) Integer amount,

    @Schema(
        description = "The settlement date for the OTC request which determines the expiry date of the optional contract",
        example = "2025-05-22"
    ) LocalDate settlementDate

) {
}
