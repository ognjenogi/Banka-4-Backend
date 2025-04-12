package rs.banka4.stock_service.service.abstraction;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;
import rs.banka4.stock_service.domain.trading.db.dtos.OtcRequestCreateDto;
import rs.banka4.stock_service.domain.trading.db.dtos.OtcRequestUpdateDto;

public interface OtcRequestService {
    Page<OtcRequest> getMyRequests(Pageable pageable, UUID myId);

    Page<OtcRequest> getMyRequestsUnread(Pageable pageable, UUID myId);

    void rejectOtc(UUID requestId);

    void updateOtc(OtcRequestUpdateDto otcRequestUpdateDto, UUID id, UUID modifiedBy);

    void createOtc(OtcRequestCreateDto otcRequestCreateDto, UUID idMy);

    OtcRequest acceptOtc(UUID requestId, UUID userId);
}
