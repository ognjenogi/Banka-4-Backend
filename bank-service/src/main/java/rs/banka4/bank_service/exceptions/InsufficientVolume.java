package rs.banka4.bank_service.exceptions;

import org.springframework.http.HttpStatus;
import rs.banka4.rafeisen.common.exceptions.BaseApiException;

public class InsufficientVolume extends BaseApiException {
    public InsufficientVolume() {
        super(HttpStatus.CONFLICT, null);
    }
}
