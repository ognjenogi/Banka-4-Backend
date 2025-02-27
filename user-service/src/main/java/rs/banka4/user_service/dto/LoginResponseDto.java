package rs.banka4.user_service.dto;


public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {}