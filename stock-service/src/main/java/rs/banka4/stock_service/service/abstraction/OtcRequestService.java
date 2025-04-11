package rs.banka4.stock_service.service.abstraction;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;

import java.util.UUID;

public interface OtcRequestService {
    Page<OtcRequest> getMyRequests(Pageable pageable,UUID myId);
    Page<OtcRequest> getMyRequestsUnread(Pageable pageable, UUID myId);
}
