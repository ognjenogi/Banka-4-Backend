package rs.banka4.bank_service.domain.taxes.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.banka4.bank_service.domain.account.db.Account;

@Entity
@Table(name = "user_tax_debts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTaxDebts {
    @Id
    @Builder.Default
    UUID id = UUID.randomUUID();

    @OneToOne(optional = false)
    Account account;

    BigDecimal debtAmount;

    BigDecimal yearlyDebtAmount;
}
