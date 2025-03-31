package rs.banka4.user_service.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.banka4.user_service.domain.user.User;
import rs.banka4.user_service.exceptions.jwt.ExpiredJwt;
import rs.banka4.user_service.exceptions.jwt.IllegalArgumentJwt;
import rs.banka4.user_service.exceptions.jwt.MalformedJwt;
import rs.banka4.user_service.exceptions.jwt.UnsupportedJwt;
import rs.banka4.user_service.security.AuthenticatedBankUserPrincipal;
import rs.banka4.user_service.security.UnauthenticatedBankUserPrincipal;
import rs.banka4.user_service.security.UserType;
import rs.banka4.user_service.service.abstraction.TokenService;

@Service
@Transactional
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh.token.expiration}")
    private long refreshExpiration;


    private final TokenService tokenService;

    public JwtUtil(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public String extractRole(String jwt) {
        return extractClaim(jwt, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwt();
        } catch (MalformedJwtException e) {
            throw new MalformedJwt();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentJwt();
        } catch (Exception e) {
            throw new UnsupportedJwt();
        }
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put(
            "role",
            user.getUserType()
                .name()
                .toLowerCase()
        );
        claims.put("privileges", user.getPrivileges());
        return generateToken(claims, user.getEmail(), jwtExpiration);
    }

    public String generateRefreshToken(
        AuthenticatedBankUserPrincipal principal,
        /* TODO(arsen): remove */
        UnauthenticatedBankUserPrincipal preAuthPrincipal,
        UserType type
    ) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(
            "role",
            type.name()
                .toLowerCase()
        );
        claims.put("id", principal.userId());

        return generateToken(claims, preAuthPrincipal.email(), refreshExpiration);
    }

    public String generateToken(
        Map<String, Object> extraClaims,
        /* TODO(arsen): remove */
        String email,
        long expiration
    ) {
        return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(email)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean validateToken(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    public void invalidateToken(String token) {
        tokenService.invalidateToken(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenInvalidated(String token) {
        return tokenService.findByToken(token)
            .isPresent();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaim(token, x -> x.get("id", String.class)));
    }
}
