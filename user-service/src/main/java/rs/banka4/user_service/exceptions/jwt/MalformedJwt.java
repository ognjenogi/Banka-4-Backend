package rs.banka4.user_service.exceptions.jwt;

import org.springframework.http.HttpStatus;
import rs.banka4.user_service.exceptions.BaseApiException;

public class MalformedJwt extends BaseApiException {
    public MalformedJwt() {
        super(HttpStatus.UNAUTHORIZED, null);
    }
}
