package rs.banka4.stock_service.exceptions;

import org.springframework.http.HttpStatus;
import rs.banka4.rafeisen.common.exceptions.BaseApiException;

public class InvalidOrderStatus extends BaseApiException {
    public InvalidOrderStatus() {
        super(HttpStatus.BAD_REQUEST, null);
    }
}
