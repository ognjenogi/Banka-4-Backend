package rs.banka4.bank_service.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.banka4.bank_service.domain.security.Security;

public interface SecurityRepository extends JpaRepository<Security, UUID> {

}
