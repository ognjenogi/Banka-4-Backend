package rs.banka4.bank_service.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.banka4.bank_service.domain.currency.db.Currency;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, UUID> {

    Currency findByCode(CurrencyCode code);
}
