package rs.banka4.bank_service.domain.assets.db;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.banka4.bank_service.domain.options.db.Asset;
import rs.banka4.bank_service.domain.user.User;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetOwnershipId {
    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Asset asset;
}
