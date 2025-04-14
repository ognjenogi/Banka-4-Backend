package rs.banka4.bank_service.exceptions;

import org.springframework.http.HttpStatus;
import rs.banka4.rafeisen.common.exceptions.BaseApiException;

public class RequiredPriceException extends BaseApiException {
    public RequiredPriceException() {
        super(HttpStatus.BAD_REQUEST, null);
    }
}
