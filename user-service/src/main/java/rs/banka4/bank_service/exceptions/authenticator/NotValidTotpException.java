package rs.banka4.bank_service.exceptions.authenticator;

import org.springframework.http.HttpStatus;
import rs.banka4.rafeisen.common.exceptions.BaseApiException;

public class NotValidTotpException extends BaseApiException {
    public NotValidTotpException() {
        super(HttpStatus.BAD_REQUEST, null);
    }
}
