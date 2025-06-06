package rs.banka4.bank_service.domain.transaction.db;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonetaryAmount {
    @Column()
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "currency")
    private CurrencyCode currency;
}
