package rs.banka4.bank_service.domain.options.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "request dto for use option route")
public record UseOptionRequest(
    @Schema(description = "option to use id") UUID optionId,
    @Schema(
        description = "user's account to take money from or give money to (depends on option type)"
    ) String accountNumber
) {
}
