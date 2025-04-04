package rs.banka4.stock_service.exceptions;

import org.springframework.http.HttpStatus;

public class NoJwtProvided extends BaseApiException {

    public NoJwtProvided() {
        super(HttpStatus.FORBIDDEN, null);
    }
}
