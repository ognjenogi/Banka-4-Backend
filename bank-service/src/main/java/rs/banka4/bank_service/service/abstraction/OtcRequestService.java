package rs.banka4.bank_service.service.abstraction;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rs.banka4.bank_service.domain.trading.db.OtcRequest;
import rs.banka4.bank_service.domain.trading.db.dtos.OtcRequestCreateDto;
import rs.banka4.bank_service.domain.trading.db.dtos.OtcRequestUpdateDto;
import rs.banka4.rafeisen.common.currency.CurrencyCode;
import rs.banka4.rafeisen.common.dto.AccountNumberDto;

public interface OtcRequestService {
    Page<OtcRequest> getMyRequests(Pageable pageable, UUID myId);

    Page<OtcRequest> getMyRequestsUnread(Pageable pageable, UUID myId);

    void rejectOtc(UUID requestId);

    void updateOtc(OtcRequestUpdateDto otcRequestUpdateDto, UUID id, UUID modifiedBy);

    void createOtc(OtcRequestCreateDto otcRequestCreateDto, UUID idMy);

    OtcRequest acceptOtc(UUID requestId, UUID userId);

    AccountNumberDto getRequiredAccount(UUID userId, CurrencyCode currencyCode, BigDecimal premium);
}
