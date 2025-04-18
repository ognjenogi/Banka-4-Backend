package rs.banka4.rafeisen.common.exceptions;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class RouteNotFound extends BaseApiException {
    public RouteNotFound(String path) {
        super(HttpStatus.NOT_FOUND, Map.of("path", path));
    }
}
