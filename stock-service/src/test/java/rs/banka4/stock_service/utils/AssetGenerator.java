package rs.banka4.stock_service.utils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import rs.banka4.stock_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.stock_service.domain.options.db.Asset;
import rs.banka4.stock_service.domain.options.db.Option;
import rs.banka4.stock_service.domain.options.db.OptionType;
import rs.banka4.stock_service.domain.security.forex.db.CurrencyCode;
import rs.banka4.stock_service.domain.security.forex.db.ForexLiquidity;
import rs.banka4.stock_service.domain.security.forex.db.ForexPair;
import rs.banka4.stock_service.domain.security.future.db.Future;
import rs.banka4.stock_service.domain.security.future.db.UnitName;
import rs.banka4.stock_service.domain.security.stock.db.Stock;

/**
 * Test-writing utility class for generating various assets.
 */
public class AssetGenerator {
    public static final UUID STOCK_EX1_UUID =
        UUID.fromString("c6a3ad44-0eee-4bd3-addf-d8f2086b6f51");
    public static final UUID STOCK_EX2_UUID =
        UUID.fromString("493ba3a3-2e84-4222-9fdf-c44e5e9ed9c6");

    public static final UUID FOREX_JPY_EUR_UUID =
        UUID.fromString("fa2f47dc-b5c9-4b0b-a536-7798af3b31a7");
    public static final UUID FOREX_EUR_USD_UUID =
        UUID.fromString("03c3c7b2-62ea-496e-8dbf-cb87da413e39");

    public static final UUID FUTURE_CRUDE_OIL_UUID =
        UUID.fromString("08bd0b34-5316-41b9-ace5-66d5654f9172");

    public static final UUID OPTION_EX1_UUID =
        UUID.fromString("65d965c1-0d7c-4506-a424-4fe81a1f6357");

    public static List<Asset> makeExampleAssets() {
        final var stock1 =
            Stock.builder()
                .id(STOCK_EX1_UUID)
                .name("Example One™")
                .ticker("EX1")
                .dividendYield(new BigDecimal("0.052"))
                .outstandingShares(325_000)
                .build();
        final var stock2 =
            Stock.builder()
                .id(STOCK_EX2_UUID)
                .name("Example Two™")
                .ticker("EX2")
                .dividendYield(new BigDecimal("0.064"))
                .outstandingShares(225_000)
                .build();

        final var settlementDate =
            OffsetDateTime.now()
                .plusYears(2);
        final var optionNameCall =
            settlementDate.format(DateTimeFormatter.ofPattern("'EX1'YYMMDD'C00170000'"));
        final var optionNamePut =
            settlementDate.format(DateTimeFormatter.ofPattern("'EX1'YYMMDD'P00170000'"));

        return List.of(
            stock1,
            stock2,

            ForexPair.builder()
                .id(FOREX_JPY_EUR_UUID)
                .baseCurrency(CurrencyCode.JPY)
                .quoteCurrency(CurrencyCode.EUR)
                .name("JPY/EUR")
                .exchangeRate(new BigDecimal("0.6181"))
                .liquidity(ForexLiquidity.HIGH)
                .build(),
            ForexPair.builder()
                .id(FOREX_EUR_USD_UUID)
                .baseCurrency(CurrencyCode.EUR)
                .quoteCurrency(CurrencyCode.USD)
                .name("EUR/USD")
                .exchangeRate(new BigDecimal("1.10"))
                .liquidity(ForexLiquidity.MEDIUM)
                .build(),

            Future.builder()
                .id(FUTURE_CRUDE_OIL_UUID)
                .name("Crude Oil")
                .contractSize(1000)
                .contractUnit(UnitName.BARREL)
                .settlementDate(
                    OffsetDateTime.now()
                        .plusMonths(13)
                )
                .build(),

            Option.builder()
                .id(OPTION_EX1_UUID)
                .name(optionNameCall)
                .ticker(optionNameCall)
                .optionType(OptionType.CALL)
                .strikePrice(new MonetaryAmount(new BigDecimal("170"), CurrencyCode.USD))
                .impliedVolatility(412.5)
                .openInterest(565)
                .settlementDate(settlementDate)
                .build(),
            Option.builder()
                .id(OPTION_EX1_UUID)
                .name(optionNamePut)
                .ticker(optionNamePut)
                .optionType(OptionType.CALL)
                .strikePrice(new MonetaryAmount(new BigDecimal("170"), CurrencyCode.USD))
                .impliedVolatility(412.5)
                .openInterest(565)
                .settlementDate(settlementDate)
                .build()
        );
    }
}
