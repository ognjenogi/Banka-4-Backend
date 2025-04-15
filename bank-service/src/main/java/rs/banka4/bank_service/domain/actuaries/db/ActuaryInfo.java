package rs.banka4.bank_service.domain.actuaries.db;

import jakarta.persistence.*;
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
    @Id
    private UUID userId;

    @Column(nullable = false)
    private boolean needApproval;

    @Column(nullable = false)
    private MonetaryAmount limit;

    @Column(nullable = false)
    private MonetaryAmount usedLimit;
}
