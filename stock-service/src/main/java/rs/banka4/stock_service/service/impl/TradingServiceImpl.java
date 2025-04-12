package rs.banka4.stock_service.service.impl;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import rs.banka4.rafeisen.common.dto.AccountNumberDto;
import rs.banka4.stock_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.stock_service.domain.options.db.Option;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;
import rs.banka4.stock_service.service.abstraction.TradingService;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class TradingServiceImpl implements TradingService {
    @Override
    public void sendPremiumAndGetOption(AccountNumberDto buyer, AccountNumberDto seller, OtcRequest otcRequest) {
        throw new NotImplementedException();
    }

    @Override
    public void buyOption(Option o, UUID userId, String userAccount) {
        throw new NotImplementedException();
    }
}
