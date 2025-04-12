package rs.banka4.stock_service.exceptions;

import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import rs.banka4.rafeisen.common.exceptions.BaseApiException;

public class OptionOwnershipNotFound extends BaseApiException {
    public OptionOwnershipNotFound(UUID assetId, UUID userId) {
        super(HttpStatus.NOT_FOUND, Map.of("optionId", assetId, "userId", userId));
    }
}
