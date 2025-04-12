package rs.banka4.stock_service.exceptions;

import org.springframework.http.HttpStatus;
import rs.banka4.rafeisen.common.exceptions.BaseApiException;

import java.util.Map;

public class OptionNotFound extends BaseApiException {

    public OptionNotFound() {
        super(HttpStatus.NOT_FOUND, null);
    }
}
