package rs.banka4.stock_service.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;

public interface OtcRequestRepository extends JpaRepository<OtcRequest, UUID> {

    @Query("SELECT o FROM OtcRequest o " +
        "WHERE o.status = 'ACTIVE' " +
        "AND (o.madeFor = :userId OR o.madeBy = :userId)")
    Page<OtcRequest> findActiveRequestsByUser(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT o FROM OtcRequest o " +
        "WHERE o.status = 'ACTIVE' " +
        "AND (o.madeFor = :userId OR o.madeBy = :userId) " +
        "AND o.modifiedBy <> :userId")
    Page<OtcRequest> findActiveUnreadRequestsByUser(@Param("userId") UUID userId, Pageable pageable);
}
