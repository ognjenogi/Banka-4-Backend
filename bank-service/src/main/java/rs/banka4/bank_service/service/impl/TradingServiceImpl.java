package rs.banka4.bank_service.service.impl;

import java.util.UUID;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import rs.banka4.bank_service.domain.options.db.Option;
import rs.banka4.bank_service.domain.trading.db.OtcRequest;
import rs.banka4.bank_service.service.abstraction.TradingService;
import rs.banka4.rafeisen.common.dto.AccountNumberDto;

@Service
public class TradingServiceImpl implements TradingService {
    @Override
    public void sendPremiumAndGetOption(
        AccountNumberDto buyer,
        AccountNumberDto seller,
        OtcRequest otcRequest
    ) {
        throw new NotImplementedException();
    }

    @Override
    public void buyOption(Option o, UUID userId, String userAccount, int amount) {
        throw new NotImplementedException();
    }

    @Override
    public void usePutOption(Option o, UUID userId, String userAccount) {
        throw new NotImplementedException();
    }

    @Override
    public void useCallOptionFromExchange(Option o, UUID userId, String userAccount) {
        throw new NotImplementedException();
    }

    @Override
    public void useCallOptionFromOtc(
        Option o,
        UUID buyerId,
        UUID sellerId,
        String buyerAccount,
        String sellerAccount,
        int amount
    ) {
        throw new NotImplementedException();
    }
}
