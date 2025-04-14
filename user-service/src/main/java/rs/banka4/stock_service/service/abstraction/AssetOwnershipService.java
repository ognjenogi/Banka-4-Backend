package rs.banka4.stock_service.service.abstraction;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rs.banka4.stock_service.domain.assets.db.AssetOwnership;
import rs.banka4.stock_service.domain.trading.dtos.PublicStocksDto;
import rs.banka4.stock_service.service.impl.TransferTo;

public interface AssetOwnershipService {
    AssetOwnership transferStock(UUID userId, UUID assetId, int amount, TransferTo transferTo);

    Page<PublicStocksDto> getPublicStocks(Pageable pageable, String token);
}
