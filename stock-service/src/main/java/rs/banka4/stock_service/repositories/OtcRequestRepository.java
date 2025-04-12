package rs.banka4.stock_service.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;
import rs.banka4.stock_service.domain.trading.db.RequestStatus;

public interface OtcRequestRepository extends JpaRepository<OtcRequest, UUID> {

    @Query(
        "SELECT o FROM OtcRequest o "
            + "WHERE o.status = 'ACTIVE' "
            + "AND (o.madeFor.userId = :userId OR o.madeBy.userId = :userId)"
    )
    Page<OtcRequest> findActiveRequestsByUser(@Param("userId") String userId, Pageable pageable);

    @Query(
        "SELECT o FROM OtcRequest o "
            + "WHERE o.status = 'ACTIVE' "
            + "AND (o.madeFor.userId = :userId OR o.madeBy.userId = :userId) "
            + "AND o.modifiedBy.userId <> :userId"
    )
    Page<OtcRequest> findActiveUnreadRequestsByUser(
        @Param("userId") String userId,
        Pageable pageable
    );

    List<OtcRequest> findAllByStatusAndSettlementDateBefore(RequestStatus status, LocalDate date);
}
