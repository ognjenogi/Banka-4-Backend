package rs.banka4.stock_service.repositories;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.banka4.stock_service.domain.orders.db.Order;
import rs.banka4.stock_service.domain.orders.db.Status;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query(
        value = "select count(*) from Order o where o.asset.id = ?1 and o.createdAt between ?2 and ?3"
    )
    int findCountOfMadeOrdersToday(UUID assetId, OffsetDateTime startDate, OffsetDateTime endDate);

    Page<Order> findAllByStatusIn(List<Status> statuses, Pageable pageable);
}
