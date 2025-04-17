package rs.banka4.bank_service.domain.taxes.db.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

@Schema(description = "Summary entry for a user eligible for capital gains tax")
public record TaxableUserDto(
    @Schema(
        description = "User ID",
        example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    ) UUID userId,

    @Schema(
        description = "First name",
        example = "John"
    ) String firstName,
    @Schema(
        description = "email",
        example = "John@gmail.com"
    ) String email,

    @Schema(
        description = "Last name",
        example = "Doe"
    ) String lastName,

    @Schema(
        description = "Current unpaid tax amount in RSD",
        example = "1200.50"
    ) BigDecimal unpaidTax,
    @Schema(
        description = "Currency of tax",
        example = "RSD"
    ) CurrencyCode currency
) {
}
