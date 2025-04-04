package rs.banka4.stock_service.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class CannotUpdateActuaryException extends BaseApiException {
    public CannotUpdateActuaryException(String id) {
        super(HttpStatus.FORBIDDEN, Map.of("id", id));
    }
}
