package rs.banka4.rafeisen.common.exception;

import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
@RequiredArgsConstructor
public class BaseApiException extends RuntimeException {
    private final int status;
    private final Map<String, Object> extra;
}
