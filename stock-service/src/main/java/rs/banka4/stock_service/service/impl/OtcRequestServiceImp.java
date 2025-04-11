package rs.banka4.stock_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;
import rs.banka4.stock_service.repositories.OtcRequestRepository;
import rs.banka4.stock_service.service.abstraction.OtcRequestService;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OtcRequestServiceImp implements OtcRequestService{
    private final OtcRequestRepository otcRequestRepository;

    @Override
    public Page<OtcRequest> getMyRequests(Pageable pageable, UUID myId) {
        return otcRequestRepository.findActiveRequestsByUser(myId, pageable);
    }

    @Override
    public Page<OtcRequest> getMyRequestsUnread(Pageable pageable, UUID myId) {
        return otcRequestRepository.findActiveUnreadRequestsByUser(myId, pageable);
    }
}
