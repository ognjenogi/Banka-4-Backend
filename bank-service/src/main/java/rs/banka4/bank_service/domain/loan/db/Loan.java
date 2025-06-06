package rs.banka4.bank_service.domain.loan.db;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import rs.banka4.bank_service.domain.account.db.Account;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "loans")
public class Loan {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(
        nullable = false,
        unique = true
    )
    private Long loanNumber;

    private BigDecimal amount;
    private Integer repaymentPeriod;
    private LocalDate agreementDate;
    private LocalDate dueDate;
    private BigDecimal monthlyInstallment;
    private LocalDate nextInstallmentDate;
    private BigDecimal remainingDebt;
    private BigDecimal baseInterestRate;

    @ManyToOne
    @JoinColumn(
        name = "interest_rate_id",
        nullable = false
    )
    private InterestRate interestRate;


    @ManyToOne
    private Account account;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @Enumerated(EnumType.STRING)
    private LoanType type;

    @Enumerated(EnumType.STRING)
    private InterestType interestType;

    public enum InterestType {
        FIXED,
        VARIABLE
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass =
            o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer()
                    .getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass =
            this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer()
                    .getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Loan loan = (Loan) o;
        return getId() != null && Objects.equals(getId(), loan.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode()
            : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Loan{" + "id=" + id + ", loanNumber=" + loanNumber + '}';
    }
}
