package rs.banka4.bank_service.domain.taxes.db.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Tax information for the user's account")
public record UserTaxInfoDto(
    @Schema(
        description = "Total tax paid this year",
        example = "860.00"
    ) BigDecimal paidTaxThisYear,
    @Schema(
        description = "Total unpaid tax for the current month",
        example = "72.50"
    ) BigDecimal unpaidTaxThisMonth,
    @Schema(
        description = "Currency of the amounts",
        example = "RSD"
    ) String currency
) {
}
