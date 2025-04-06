package rs.banka4.stock_service.exceptions;

import java.util.Map;
import org.springframework.http.HttpStatus;
import rs.banka4.rafeisen.common.exceptions.BaseApiException;

public class ActuaryNotFoundException extends BaseApiException {
    public ActuaryNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, Map.of("id", id));
    }
}
