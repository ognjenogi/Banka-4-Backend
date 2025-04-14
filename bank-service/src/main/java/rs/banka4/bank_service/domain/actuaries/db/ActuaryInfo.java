package rs.banka4.bank_service.domain.actuaries.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "actuary_informations")
@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class ActuaryInfo {
    /** here should be user id that is actuary (trust me bro key) */
    @Id
    @Column(
        unique = true,
        nullable = false
    )
    private UUID userId;

    @Column(nullable = false)
    private boolean needApproval;

    @Column(nullable = false)
    private MonetaryAmount limit;

    @Column(nullable = false)
    private MonetaryAmount usedLimit;
}
