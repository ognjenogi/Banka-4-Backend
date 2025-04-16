package rs.banka4.bank_service.service.abstraction;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.security.SecurityDto;
import rs.banka4.bank_service.domain.security.responses.SecurityHoldingDto;


public interface SecuritiesService {
    // Object is StockDto, FutureDto or ForexDto
    ResponseEntity<Page<SecurityDto>> getSecurities(
        String securityType,
        String name,
        Pageable pageable
    );

    Page<SecurityHoldingDto> getMyPortfolio(UUID myId, Pageable pageable);

    MonetaryAmount calculateTotalProfit(UUID myId);
}
