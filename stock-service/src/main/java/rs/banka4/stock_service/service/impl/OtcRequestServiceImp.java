package rs.banka4.stock_service.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rs.banka4.stock_service.domain.trading.db.OtcMapper;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;
import rs.banka4.stock_service.domain.trading.db.RequestStatus;
import rs.banka4.stock_service.domain.trading.db.dtos.OtcRequestUpdateDto;
import rs.banka4.stock_service.exceptions.OtcNotFoundException;
import rs.banka4.stock_service.exceptions.RequestFailed;
import rs.banka4.stock_service.repositories.OtcRequestRepository;
import rs.banka4.stock_service.service.abstraction.OtcRequestService;

@Service
@RequiredArgsConstructor
public class OtcRequestServiceImp implements OtcRequestService {
    private final OtcRequestRepository otcRequestRepository;
    private final OtcMapper otcMapper;

    @Override
    public Page<OtcRequest> getMyRequests(Pageable pageable, UUID myId) {
        return otcRequestRepository.findActiveRequestsByUser(myId.toString(), pageable);
    }

    @Override
    public Page<OtcRequest> getMyRequestsUnread(Pageable pageable, UUID myId) {
        return otcRequestRepository.findActiveUnreadRequestsByUser(myId.toString(), pageable);
    }

    @Override
    public void rejectOtc(UUID requestId) {
        var otc =
            otcRequestRepository.findById(requestId)
                .orElseThrow(() -> new OtcNotFoundException(requestId));
        if (
            !otc.getStatus()
                .equals(RequestStatus.ACTIVE)
        ) throw new RequestFailed();
        otc.setStatus(RequestStatus.REJECTED);
        otcRequestRepository.save(otc);
    }

    @Override
    public void updateOtc(OtcRequestUpdateDto otcRequestUpdateDto, UUID id) {
        var otc = otcRequestRepository.findById(id).orElseThrow(() -> new OtcNotFoundException(id));
        otcMapper.update(otc, otcRequestUpdateDto);
        otcRequestRepository.save(otc);
    }
}
