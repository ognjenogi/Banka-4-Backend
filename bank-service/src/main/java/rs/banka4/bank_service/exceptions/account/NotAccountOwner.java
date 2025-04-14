package rs.banka4.bank_service.exceptions.account;

import org.springframework.http.HttpStatus;
import rs.banka4.rafeisen.common.exceptions.BaseApiException;

public class NotAccountOwner extends BaseApiException {
    public NotAccountOwner() {
        super(HttpStatus.FORBIDDEN, null);
    }
}
