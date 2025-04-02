package rs.banka4.user_service.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.banka4.rafeisen.common.exception.MalformedJwt;
import rs.banka4.rafeisen.common.exception.UnsupportedJwt;
import rs.banka4.rafeisen.common.security.UserType;
import rs.banka4.rafeisen.common.utils.jwt.JwtTokenGenerator;
import rs.banka4.rafeisen.common.utils.jwt.JwtUtil;
import rs.banka4.rafeisen.common.utils.jwt.UnverifiedToken;
import rs.banka4.user_service.domain.user.User;
import rs.banka4.user_service.exceptions.jwt.ExpiredJwt;
import rs.banka4.user_service.exceptions.jwt.IllegalArgumentJwt;
import rs.banka4.user_service.security.AuthenticatedBankUserPrincipal;
import rs.banka4.user_service.security.UnauthenticatedBankUserPrincipal;
import rs.banka4.user_service.service.abstraction.JwtService;
import rs.banka4.user_service.service.abstraction.TokenService;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    @Value("${jwt.refresh.token.expiration}")
    private int refreshExpiration;

    @Value("${jwt.version}")
    private int jwtVersion;

    private final TokenService tokenService;
    private JwtTokenGenerator jwtTokenGenerator;

    @PostConstruct
    public void init() {
        Key key =
            io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey)
            );
        this.jwtTokenGenerator =
            new JwtTokenGenerator(key, jwtExpiration, refreshExpiration, jwtVersion);
    }

    @Override
    public UnverifiedToken parseToken(String jwt) {
        try {
            return JwtUtil.parseToken(jwt);
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

    @Override
    public String generateAccessToken(User user) {
        return jwtTokenGenerator.generateAccessToken(
            user.getId()
                .toString(),
            user.getUserType()
                .name()
                .toUpperCase(),
            user.getPrivileges()
                .stream()
                .map(Object::toString)
                .toList()
        );
    }

    @Override
    public String generateRefreshToken(
        AuthenticatedBankUserPrincipal principal,
        UnauthenticatedBankUserPrincipal preAuthPrincipal,
        UserType type
    ) {
        return jwtTokenGenerator.generateRefreshToken(
            principal.userId()
                .toString(),
            type.name()
                .toUpperCase()
        );
    }

    @Override
    public boolean isTokenExpired(String token) {
        UnverifiedToken unverifiedToken = parseToken(token);
        return unverifiedToken.getExp()
            .before(new Date());
    }

    @Override
    public boolean isTokenInvalidated(String token) {
        return tokenService.findByToken(token)
            .isPresent();
    }

    @Override
    public void invalidateToken(String token) {
        tokenService.invalidateToken(token);
    }

    @Override
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    @Override
    public String extractRole(String token) {
        UnverifiedToken tokenObj = parseToken(token);
        return Optional.ofNullable(tokenObj.getRole())
            .map(Object::toString)
            .orElse(null);
    }

    @Override
    public UUID extractUserId(String token) {
        UnverifiedToken tokenObj = parseToken(token);
        Object id = tokenObj.getSub();
        return UUID.fromString(id.toString());
    }
}
