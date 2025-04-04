package rs.banka4.stock_service.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ActuaryNotFoundException extends BaseApiException {
    public ActuaryNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, Map.of("id", id));
    }
}
