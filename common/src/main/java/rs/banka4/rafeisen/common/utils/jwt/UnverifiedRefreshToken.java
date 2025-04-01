package rs.banka4.rafeisen.common.utils.jwt;

import io.jsonwebtoken.Claims;


public class UnverifiedRefreshToken extends UnverifiedToken {

    protected String role;

    public UnverifiedRefreshToken(Claims claims) {
        super(claims);
        this.role = (String) claims.get("role");
    }
}
