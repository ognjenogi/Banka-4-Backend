package rs.banka4.bank_service.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.banka4.bank_service.domain.taxes.db.UserTaxDebts;

public interface UserTaxDebtsRepository extends JpaRepository<UserTaxDebts, UUID> {
    List<UserTaxDebts> findByAccount_Client_Id(UUID userId);
}
