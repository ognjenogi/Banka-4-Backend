package rs.banka4.rafeisen.common.utils.jwt;

import io.jsonwebtoken.Claims;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class UnverifiedToken {

    protected String jti;
    protected String sub;
    protected Date exp;
    protected String typ;
    protected Integer ver;

    protected UnverifiedToken(Claims claims) {
        this.jti = claims.getId();
        this.sub = claims.getSubject();
        this.exp = claims.getExpiration();
        this.typ = (String) claims.get("typ");
        Object verClaim = claims.get("ver");
        this.ver = (verClaim instanceof Number) ? ((Number) verClaim).intValue() : null;
    }
}
