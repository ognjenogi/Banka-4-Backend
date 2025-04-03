package rs.banka4.stock_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.banka4.stock_service.domain.actuaries.db.ActuaryInfo;

import java.util.List;
import java.util.UUID;

public interface ActuaryRepository extends JpaRepository<ActuaryInfo, UUID> {
    //Supervisors dont need approval while agents do
    List<ActuaryInfo> findByNeedApprovalTrue();
}
