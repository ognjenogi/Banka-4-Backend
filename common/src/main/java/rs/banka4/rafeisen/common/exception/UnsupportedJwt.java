package rs.banka4.rafeisen.common.exception;

public class UnsupportedJwt extends BaseApiException {
    public UnsupportedJwt() {
        super(HttpStatus.UNAUTHORIZED.getCode(), null);
    }
}
