package rs.banka4.stock_service.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rs.banka4.stock_service.domain.security.stock.db.Stock;
import rs.banka4.stock_service.domain.trading.db.ForeignBankId;
import rs.banka4.stock_service.domain.trading.db.OtcMapper;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;
import rs.banka4.stock_service.domain.trading.db.RequestStatus;
import rs.banka4.stock_service.domain.trading.db.dtos.OtcRequestCreateDto;
import rs.banka4.stock_service.domain.trading.db.dtos.OtcRequestUpdateDto;
import rs.banka4.stock_service.domain.trading.utill.BankRoutingNumber;
import rs.banka4.stock_service.exceptions.OtcNotFoundException;
import rs.banka4.stock_service.exceptions.RequestFailed;
import rs.banka4.stock_service.exceptions.StockOwnershipNotFound;
import rs.banka4.stock_service.repositories.AssetOwnershipRepository;
import rs.banka4.stock_service.repositories.OtcRequestRepository;
import rs.banka4.stock_service.service.abstraction.OtcRequestService;

@Service
@RequiredArgsConstructor
public class OtcRequestServiceImp implements OtcRequestService {
    private final OtcRequestRepository otcRequestRepository;
    private final OtcMapper otcMapper;
    private final AssetOwnershipRepository assetOwnershipRepository;

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
    public void updateOtc(OtcRequestUpdateDto otcRequestUpdateDto, UUID id, UUID modifiedBy) {
        var otc =
            otcRequestRepository.findById(id)
                .orElseThrow(() -> new OtcNotFoundException(id));
        var modBy =
            new ForeignBankId(BankRoutingNumber.BANK4.getRoutingNumber(), modifiedBy.toString());
        otcMapper.update(otc, otcRequestUpdateDto, modBy);
        otcRequestRepository.save(otc);
    }

    @Override
    public void createOtc(OtcRequestCreateDto otcRequestCreateDto, UUID idMy) {
        var assetOwner =
            assetOwnershipRepository.findByMyId(
                otcRequestCreateDto.userId(),
                otcRequestCreateDto.assetId()
            )
                .orElseThrow(
                    () -> new StockOwnershipNotFound(
                        otcRequestCreateDto.userId(),
                        otcRequestCreateDto.assetId()
                    )
                );
        if (assetOwner.getPublicAmount() < otcRequestCreateDto.amount()) throw new RequestFailed();
        var me = new ForeignBankId(BankRoutingNumber.BANK4.getRoutingNumber(), idMy.toString());
        var madeFor =
            new ForeignBankId(
                BankRoutingNumber.BANK4.getRoutingNumber(),
                assetOwner.getId()
                    .getUser()
                    .toString()
            );
        var stock =
            (Stock) assetOwner.getId()
                .getAsset();
        var newOtc =
            otcMapper.toOtcRequest(
                otcRequestCreateDto,
                me,
                madeFor,
                me,
                RequestStatus.ACTIVE,
                stock
            );
        otcRequestRepository.save(newOtc);
    }
}
