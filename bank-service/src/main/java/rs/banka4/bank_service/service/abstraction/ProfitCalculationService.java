package rs.banka4.bank_service.service.abstraction;

import java.util.UUID;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.options.db.Asset;
import rs.banka4.bank_service.domain.options.db.Option;

public interface ProfitCalculationService {
    /**
     * Calculates total profit (unrealized) for the given user and asset. In case of option pls give
     * current price of stock that option has.
     */
    MonetaryAmount calculateProfit(UUID userId, Asset asset, MonetaryAmount currentPrice);

    /**
     * Calculates profit (unrealized/realized) for the given option.
     */
    MonetaryAmount calculateOptionProfit(Option option);
}
