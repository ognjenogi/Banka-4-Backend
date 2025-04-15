package rs.banka4.bank_service.domain.account.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

public record BankAccountDto(
    @Schema(
        description = "Account number",
        example = "1234567890"
    ) String accountNumber,
    @NotNull(message = "Currency ID cannot be null")

    @Schema(
        description = "Currency ID associated with this account",
        example = "RSD"
    ) CurrencyCode currency,

    @Schema(
        description = "Current balance",
        example = "1000.00"
    ) BigDecimal balance,

    @Schema(
        description = "Available balance",
        example = "800.00"
    ) BigDecimal availableBalance
) {
}
