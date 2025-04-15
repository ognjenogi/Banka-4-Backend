package rs.banka4.bank_service.domain.orders.db;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import rs.banka4.bank_service.domain.account.db.Account;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.options.db.Asset;
import rs.banka4.bank_service.domain.user.User;

@Entity
@Builder
@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
public class Order {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Asset asset;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int contractSize;

    @Column(nullable = false)
    private MonetaryAmount pricePerUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Direction direction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private UUID approvedBy;

    @Column(nullable = false)
    private boolean isDone;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime lastModified;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private int remainingPortions;

    @Column(nullable = false)
    private boolean afterHours;

    private MonetaryAmount limitValue;

    private MonetaryAmount stopValue;

    @Column(nullable = false)
    private boolean allOrNothing;

    @Column(nullable = false)
    private boolean margin;

    @ManyToOne(optional = false)
    private Account account;

    /** did user who bought it, used it */
    @Column(nullable = false)
    private boolean used;
}
