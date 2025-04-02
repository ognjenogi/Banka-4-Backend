package rs.banka4.rafeisen.common.utils.jwt;

import io.jsonwebtoken.Claims;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnverifiedAccessToken extends UnverifiedToken {

    private List<String> privileges;
    protected String role;

    public UnverifiedAccessToken(Claims claims) {
        super(claims);
        Object privClaim = claims.get("privileges");
        if (privClaim instanceof List<?>) {
            this.privileges = (List<String>) privClaim;
        }
        this.role = (String) claims.get("role");
    }
}
