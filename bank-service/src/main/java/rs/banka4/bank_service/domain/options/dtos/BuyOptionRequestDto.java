package rs.banka4.bank_service.domain.options.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "request dto to buy an option")
public record BuyOptionRequestDto(
    @Schema(description = "option to buy id") UUID optionId,
    @Schema(description = "user's account to take premium amount from") String accountNumber,
    @Schema(description = "amount of options i want to buy for this stock") int amount
) {
}
