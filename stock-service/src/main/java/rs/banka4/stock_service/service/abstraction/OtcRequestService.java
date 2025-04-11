package rs.banka4.stock_service.service.abstraction;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;

public interface OtcRequestService {
    Page<OtcRequest> getMyRequests(Pageable pageable, UUID myId);

    Page<OtcRequest> getMyRequestsUnread(Pageable pageable, UUID myId);

    void rejectOtc(UUID requestId);
}
