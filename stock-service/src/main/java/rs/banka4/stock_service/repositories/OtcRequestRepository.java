package rs.banka4.stock_service.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;

public interface OtcRequestRepository extends JpaRepository<OtcRequest, UUID> {
}
