package rs.banka4.stock_service.domain.security;

import jakarta.persistence.*;
import java.util.Objects;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import rs.banka4.stock_service.domain.options.db.Asset;


@Getter
@Setter
@RequiredArgsConstructor
@SuperBuilder
@Entity(name = "securities")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Security extends Asset {


    @Override
    public boolean equals(Object o) {
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
        Security security = (Security) o;
        return getId() != null && Objects.equals(getId(), security.getId());
    }

    @Override
    public int hashCode() {
        return this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode()
            : getClass().hashCode();
    }

    public String toString() {
        return "Security(id=" + this.getName() + ", " + getId() + ")";
    }
}
