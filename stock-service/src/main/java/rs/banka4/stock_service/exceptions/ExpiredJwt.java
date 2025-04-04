package rs.banka4.stock_service.exceptions;

import org.springframework.http.HttpStatus;

public class ExpiredJwt extends BaseApiException {
    public ExpiredJwt() {
        super(HttpStatus.UNAUTHORIZED, null);
    }
}
