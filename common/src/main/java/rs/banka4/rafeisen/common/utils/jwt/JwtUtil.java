package rs.banka4.rafeisen.common.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JwtUtil {

    /**
     * Parses a JWT string (JWS or JWT) without verifying the signature. Returns an UnverifiedToken
     * (Access or Refresh) with claims.
     */
    public static UnverifiedToken parseToken(String jwt) {

        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            throw new JwtException("Malformed JWT token");
        }

        String unsignedToken = parts[0] + "." + parts[1] + ".";

        Claims claims;
        claims =
            Jwts.parserBuilder()
                .build()
                .parseClaimsJwt(unsignedToken)
                .getBody();

        String tokenType = (String) claims.get("typ");
        UnverifiedToken token;
        if ("ACCESS".equalsIgnoreCase(tokenType)) {
            token = new UnverifiedAccessToken(claims);
        } else if ("REFRESH".equalsIgnoreCase(tokenType)) {
            token = new UnverifiedRefreshToken(claims);
        } else {
            throw new JwtException("Unknown token type: " + tokenType);
        }
        return token;
    }
}
