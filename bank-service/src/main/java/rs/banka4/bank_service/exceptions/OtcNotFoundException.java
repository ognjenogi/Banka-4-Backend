package rs.banka4.bank_service.exceptions;

import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import rs.banka4.rafeisen.common.exceptions.BaseApiException;

public class OtcNotFoundException extends BaseApiException {
    public OtcNotFoundException(UUID id) {
        super(HttpStatus.NOT_FOUND, Map.of("id", id));
    }
}
