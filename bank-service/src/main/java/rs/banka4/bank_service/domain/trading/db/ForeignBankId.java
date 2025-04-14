package rs.banka4.bank_service.domain.trading.db;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ForeignBankId(
    /**
     * Bank identifier: example for our bank - 444
     */
    @Column(nullable = false) long routingNumber,
    @Column(nullable = false) String userId
) {
}
