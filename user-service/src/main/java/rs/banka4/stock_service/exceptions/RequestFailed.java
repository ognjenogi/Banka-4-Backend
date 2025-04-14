package rs.banka4.stock_service.exceptions;

import org.springframework.http.HttpStatus;
import rs.banka4.rafeisen.common.exceptions.BaseApiException;

public class RequestFailed extends BaseApiException {
    public RequestFailed() {
        super(HttpStatus.BAD_REQUEST, null);
    }
}
