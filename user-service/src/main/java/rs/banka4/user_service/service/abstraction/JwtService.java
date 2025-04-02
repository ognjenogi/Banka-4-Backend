package rs.banka4.user_service.service.abstraction;

import java.util.UUID;
import rs.banka4.rafeisen.common.security.UserType;
import rs.banka4.rafeisen.common.utils.jwt.UnverifiedToken;
import rs.banka4.user_service.domain.user.User;
import rs.banka4.user_service.security.AuthenticatedBankUserPrincipal;
import rs.banka4.user_service.security.UnauthenticatedBankUserPrincipal;

public interface JwtService {
    UnverifiedToken parseToken(String jwt);

    String generateAccessToken(User user);

    String generateRefreshToken(
        AuthenticatedBankUserPrincipal principal,
        UnauthenticatedBankUserPrincipal preAuthPrincipal,
        UserType type
    );

    boolean validateToken(String token);

    void invalidateToken(String token);

    boolean isTokenExpired(String token);

    boolean isTokenInvalidated(String token);

    String extractRole(String token);

    UUID extractUserId(String token);
}
