package rs.banka4.bank_service.integration.services;

import static rs.banka4.rafeisen.common.currency.CurrencyCode.*;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import rs.banka4.bank_service.domain.exchange.dtos.ExchangeRate;
import rs.banka4.bank_service.domain.exchange.dtos.ExchangeRateDto;
import rs.banka4.bank_service.service.abstraction.ExchangeRateService;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

@Service
@Profile("test")
public class TestExchangeRateService extends ExchangeRateService {
    private Map.Entry<CurrencyCode, ExchangeRate> e(
        CurrencyCode code,
        String buy,
        String neutral,
        String sell
    ) {
        return Map.entry(
            code,
            new ExchangeRate(
                code.name(),
                RSD.name(),
                new BigDecimal(buy),
                new BigDecimal(neutral),
                new BigDecimal(sell)
            )
        );
    }

    @Override
    public ExchangeRateDto getExchangeRates() {
        /* A snapshot. */
        return new ExchangeRateDto(
            "2025-04-15T00:00:01+00:00",
            1744675201,
            "2025-04-16T00:00:01+00:00",
            1744761601,
            1744717587,
            Map.ofEntries(
                e(USD, "102.1039603960396", "103.13531353135313", "104.16666666666667"),
                e(EUR, "116.06096131301291", "117.2332942555686", "118.40562719812428"),
                e(GBP, "134.52914798206277", "135.88802826470987", "137.24690854735698"),
                e(JPY, "0.7126403685574432", "0.7198387561186295", "0.7270371436798158"),
                e(AUD, "64.32748538011697", "64.97725795971411", "65.62703053931125"),
                e(CAD, "73.55126300148589", "74.29420505200595", "75.037147102526"),
                e(CHF, "124.66943709860219", "125.92872434202242", "127.18801158544265")
            )
        );
    }
}
