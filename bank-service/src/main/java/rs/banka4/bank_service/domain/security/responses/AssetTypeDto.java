package rs.banka4.bank_service.domain.security.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The different types of assets available.")
public enum AssetTypeDto {
    STOCK,
    FUTURE,
    FOREX_PAIR,
    OPTION
}
