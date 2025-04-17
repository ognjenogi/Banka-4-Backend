package rs.banka4.bank_service.repositories;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.banka4.bank_service.domain.taxes.db.UserTaxDebts;
import rs.banka4.bank_service.domain.user.client.db.Client;

public interface UserTaxDebtsRepository extends JpaRepository<UserTaxDebts, UUID> {
    List<UserTaxDebts> findByAccount_Client_Id(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserTaxDebts> findByAccount_AccountNumber(String accountId);

    /**
     * Finds all distinct clients who have at least one UserTaxDebts record with debtAmount > 0.
     * Optionally filters by firstName / lastName (partial, case‑insensitive).
     *
     * @param firstNameFilter partial match on client.firstName (or null/empty to skip)
     * @param lastNameFilter partial match on client.lastName (or null/empty to skip)
     */
    @Query("""
          SELECT DISTINCT c
          FROM UserTaxDebts d
          JOIN d.account a
          JOIN a.client c
          WHERE d.debtAmount > 0
            AND (
              :firstNameFilter IS NULL OR :firstNameFilter = ''
              OR UPPER(c.firstName) LIKE UPPER(CONCAT('%', :firstNameFilter, '%'))
            )
            AND (
              :lastNameFilter IS NULL OR :lastNameFilter = ''
              OR UPPER(c.lastName)  LIKE UPPER(CONCAT('%', :lastNameFilter, '%'))
            )
        """)
    Page<Client> findClientsWithDebt(
        @Param("firstNameFilter") String firstNameFilter,
        @Param("lastNameFilter") String lastNameFilter,
        Pageable pageable
    );
}
