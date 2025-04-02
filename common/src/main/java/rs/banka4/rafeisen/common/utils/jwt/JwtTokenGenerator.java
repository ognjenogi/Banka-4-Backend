package rs.banka4.rafeisen.common.utils.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import java.util.List;

public class JwtTokenGenerator {

    private final Key signingKey;
    private final int accessTokenValiditySeconds;
    private final int refreshTokenValiditySeconds;
    private final int currentVersion;

    public JwtTokenGenerator(
        Key signingKey,
        int accessTokenValiditySeconds,
        int refreshTokenValiditySeconds,
        int currentVersion
    ) {
        this.signingKey = signingKey;
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
        this.currentVersion = currentVersion;
    }

    public String generateAccessToken(String userId, String role, List<String> privileges) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenValiditySeconds * 1000L);
        return Jwts.builder()
            .setSubject(userId)
            .setExpiration(exp)
            .claim("typ", "ACCESS")
            .claim("ver", currentVersion)
            .claim("role", role)
            .claim("privileges", privileges)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken(String userId, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshTokenValiditySeconds * 1000L);
        return Jwts.builder()
            .setSubject(userId)
            .setExpiration(exp)
            .claim("typ", "REFRESH")
            .claim("ver", currentVersion)
            .claim("role", role)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }
}
