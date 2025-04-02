package rs.banka4.rafeisen.common.utils.jwt;

import io.jsonwebtoken.Claims;


public class UnverifiedRefreshToken extends UnverifiedToken {
    public UnverifiedRefreshToken(Claims claims) {
        super(claims);
    }
}
