package rs.banka4.user_service.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.banka4.user_service.domain.user.User;

public interface UserRepository extends JpaRepository<User, UUID> {
}
