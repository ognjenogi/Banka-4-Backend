package rs.banka4.bank_service.repositories;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.banka4.bank_service.domain.exchanges.db.Exchange;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, UUID> {
    Optional<Exchange> findByExchangeAcronym(String exchangeAcronym);
}
