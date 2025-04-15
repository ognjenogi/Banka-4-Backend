package rs.banka4.bank_service.service.abstraction;

import java.util.UUID;
import rs.banka4.bank_service.domain.user.User;
import rs.banka4.rafeisen.common.security.AuthenticatedBankUserPrincipal;
import rs.banka4.rafeisen.common.utils.jwt.UnverifiedToken;
import rs.banka4.rafeisen.common.utils.jwt.VerifiedToken;

public interface JwtService {
    String generateAccessToken(User user);

    String generateRefreshToken(AuthenticatedBankUserPrincipal principal);

    boolean validateToken(String token);

    void invalidateToken(String token);

    boolean isTokenExpired(String token);

    boolean isTokenInvalidated(String token);

    String extractRole(String token);

    UUID extractUserId(String token);

    VerifiedToken parseToken(UnverifiedToken token);
}
