package rs.banka4.rafeisen.common.exception;

public class MalformedJwt extends BaseApiException {
    public MalformedJwt() {
        super(HttpStatus.UNAUTHORIZED.getCode(), null);
    }
}
