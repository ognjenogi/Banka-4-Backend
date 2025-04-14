package rs.banka4.bank_service.exceptions.card;

import org.springframework.http.HttpStatus;
import rs.banka4.rafeisen.common.exceptions.BaseApiException;

public class NotValidCardStatus extends BaseApiException {
    public NotValidCardStatus() {
        super(HttpStatus.BAD_REQUEST, null);
    }
}
