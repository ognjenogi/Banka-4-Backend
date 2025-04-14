package rs.banka4.bank_service.exceptions.loan;

import org.springframework.http.HttpStatus;
import rs.banka4.rafeisen.common.exceptions.BaseApiException;

public class LoanNotFound extends BaseApiException {

    public LoanNotFound() {
        super(HttpStatus.NOT_FOUND, null);
    }
}
