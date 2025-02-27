package rs.banka4.user_service.dto;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenDto (
        @NotNull
        String refreshToken
) {
}
