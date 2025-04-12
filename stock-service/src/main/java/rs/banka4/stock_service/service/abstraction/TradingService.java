package rs.banka4.stock_service.service.abstraction;

import rs.banka4.rafeisen.common.dto.AccountNumberDto;
import rs.banka4.stock_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.stock_service.domain.options.db.Option;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface TradingService {
    void sendPremiumAndGetOption(AccountNumberDto buyer, AccountNumberDto seller, OtcRequest otcRequest);

    void buyOption(Option o, UUID userId, String userAccount);
}
