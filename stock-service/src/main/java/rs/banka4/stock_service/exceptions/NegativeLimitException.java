package rs.banka4.stock_service.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class NegativeLimitException extends BaseApiException{
    public NegativeLimitException(String id) {
        super(HttpStatus.FORBIDDEN, Map.of("id", id));
    }
}
