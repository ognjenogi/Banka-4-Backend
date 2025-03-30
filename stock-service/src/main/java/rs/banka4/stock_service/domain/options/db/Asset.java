package rs.banka4.stock_service.domain.options.db;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@SuperBuilder
public abstract class Asset {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();
}
