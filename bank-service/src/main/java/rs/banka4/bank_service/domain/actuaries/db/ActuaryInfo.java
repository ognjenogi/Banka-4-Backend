package rs.banka4.bank_service.domain.actuaries.db;

import jakarta.persistence.*;
import lombok.*;
import rs.banka4.bank_service.domain.user.User;

@Entity
@Table(name = "actuary_informations")
@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class ActuaryInfo {
    @Id
    @OneToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private boolean needApproval;

    @Column(nullable = false)
    private MonetaryAmount limit;

    @Column(nullable = false)
    private MonetaryAmount usedLimit;
}
