package rs.banka4.stock_service.exceptions;

import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import rs.banka4.rafeisen.common.exceptions.BaseApiException;

public class CantAcceptThisOffer extends BaseApiException {
    public CantAcceptThisOffer(String message, UUID id) {
        super(HttpStatus.BAD_REQUEST, Map.of(message, id));
    }
}
