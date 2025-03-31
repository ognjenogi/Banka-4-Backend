package rs.banka4.stock_service.runners;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import rs.banka4.stock_service.domain.exchanges.db.Exchange;
import rs.banka4.stock_service.domain.security.forex.db.CurrencyCode;
import rs.banka4.stock_service.domain.security.forex.db.ForexLiquidity;
import rs.banka4.stock_service.domain.security.forex.db.ForexPair;
import rs.banka4.stock_service.domain.security.future.db.Future;
import rs.banka4.stock_service.domain.security.future.db.UnitName;
import rs.banka4.stock_service.domain.security.stock.db.Stock;
import rs.banka4.stock_service.repositories.*;

@Component
@RequiredArgsConstructor
public class TestDataRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestDataRunner.class);

    private final Environment environment;
    private final ForexRepository forexPairRepository;
    private final StockRepository stockRepository;
    private final FutureRepository futureRepository;
    private final ListingRepository listingRepository;
    private final ExchangeRepository exchangeRepository;
    private final ListingDailyPriceInfoRepository listingDailyPriceInfoRepository;

    private static final UUID STOCK_APPLE = UUID.fromString("67681711-43C0-413C-B7EC-EF78AB19BB71");
    private static final UUID STOCK_MICROSOFT =
        UUID.fromString("7E5CD697-7E53-4855-82F9-EB241E614ECA");
    private static final UUID STOCK_AMAZON =
        UUID.fromString("8AFF460F-1636-4CD2-BEDC-909C7E00BC35");
    private static final UUID STOCK_TESLA = UUID.fromString("13A4391A-4F1C-4883-A5D3-1A82719E9DD2");
    private static final UUID STOCK_NVIDIA =
        UUID.fromString("E8A56709-F90A-421E-A17B-0C20D9A2D9AA");
    private static final UUID FOREX_USD_EUR =
        UUID.fromString("95F5A021-6A9F-4622-ADEF-10572CE83974");
    private static final UUID FOREX_EUR_USD =
        UUID.fromString("44D70B7F-A601-49BA-B279-83B8186D3EC5");
    private static final UUID FUTURE_CRUDE_OIL =
        UUID.fromString("1FC71F44-943D-42A4-BD04-FF4D10D474F0");
    private static final UUID EXCHANGE_NASDAQ =
        UUID.fromString("E3A645C4-68CE-4DE3-A597-2C55FA4E4E78");

    @Override
    public void run(String... args) {

        seedProductionStocks();
        seedProductionForexPairs();
        seedProductionFutures();
        seedProductionExchanges();

        if (environment.matchesProfiles("dev")) {
            LOGGER.info("Inserting fake data (profiles includes 'dev')");
            seedDevStocks();
            seedDevForexPairs();
            seedDevFutures();
            seedDevExchanges();
        }
    }

    private void seedProductionStocks() {
        List<Stock> productionStocks =
            List.of(
                Stock.builder()
                    .id(STOCK_APPLE)
                    .name("Apple")
                    .outstandingShares(16000000)
                    .dividendYield(new BigDecimal("0.005"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_MICROSOFT)
                    .name("Microsoft")
                    .outstandingShares(7500000000L)
                    .dividendYield(new BigDecimal("0.008"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_AMAZON)
                    .name("Amazon")
                    .outstandingShares(1020000000)
                    .dividendYield(new BigDecimal("0"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_TESLA)
                    .name("Tesla")
                    .outstandingShares(3200000000L)
                    .dividendYield(new BigDecimal("0"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_NVIDIA)
                    .name("Nvidia")
                    .outstandingShares(2500000000L)
                    .dividendYield(new BigDecimal("0.002"))
                    .createdAt(OffsetDateTime.now())
                    .build()
            );
        stockRepository.saveAllAndFlush(productionStocks);
        LOGGER.info("Production stocks seeded successfully");
    }

    private void seedDevStocks() {
        if (stockRepository.count() < 37) {
            List<Stock> devStocks =
                List.of(
                    Stock.builder()
                        .id(UUID.fromString("74A6489D-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("Google (Alphabet)")
                        .outstandingShares(600000000)
                        .dividendYield(new BigDecimal("0"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A6489E-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("Meta (Facebook)")
                        .outstandingShares(2700000000L)
                        .dividendYield(new BigDecimal("0"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A6489F-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("Berkshire Hathaway")
                        .outstandingShares(2200000000L)
                        .dividendYield(new BigDecimal("0"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A648A0-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("Johnson & Johnson")
                        .outstandingShares(2600000000L)
                        .dividendYield(new BigDecimal("0.025"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A648A5-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("JPMorgan Chase")
                        .outstandingShares(2900000000L)
                        .dividendYield(new BigDecimal("0.029"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A648A6-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("Visa")
                        .outstandingShares(2200000000L)
                        .dividendYield(new BigDecimal("0.008"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A648A7-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("Procter & Gamble")
                        .outstandingShares(2500000000L)
                        .dividendYield(new BigDecimal("0.026"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A648A9-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("UnitedHealth Group")
                        .outstandingShares(900000000)
                        .dividendYield(new BigDecimal("0.016"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A648AA-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("ExxonMobil")
                        .outstandingShares(4000000000L)
                        .dividendYield(new BigDecimal("0.031"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A648AB-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("Walmart")
                        .outstandingShares(2800000000L)
                        .dividendYield(new BigDecimal("0.014"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A648AC-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("Pfizer")
                        .outstandingShares(5600000000L)
                        .dividendYield(new BigDecimal("0.046"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A648AE-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("Chevron")
                        .outstandingShares(1900000000L)
                        .dividendYield(new BigDecimal("0.041"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A648AF-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("Coca-Cola")
                        .outstandingShares(4300000000L)
                        .dividendYield(new BigDecimal("0.029"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A648B0-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("Disney")
                        .outstandingShares(1800000000L)
                        .dividendYield(new BigDecimal("0"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A648B1-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("PepsiCo")
                        .outstandingShares(1400000000L)
                        .dividendYield(new BigDecimal("0.028"))
                        .createdAt(OffsetDateTime.now())
                        .build(),
                    Stock.builder()
                        .id(UUID.fromString("74A648B2-0DBD-11F0-B4C8-0800200C9A66"))
                        .name("Intel")
                        .outstandingShares(4200000000L)
                        .dividendYield(new BigDecimal("0.016"))
                        .createdAt(OffsetDateTime.now())
                        .build()
                );
            for (Stock stock : devStocks) {
                if (!stockRepository.existsById(stock.getId())) {
                    stockRepository.saveAndFlush(stock);
                }
            }
        }
    }

    private void seedProductionForexPairs() {
        List<ForexPair> prodForexPairs =
            List.of(
                ForexPair.builder()
                    .id(FOREX_USD_EUR)
                    .baseCurrency(CurrencyCode.USD)
                    .quoteCurrency(CurrencyCode.EUR)
                    .name("USD/EUR")
                    .exchangeRate(new BigDecimal("0.90"))
                    .liquidity(ForexLiquidity.HIGH)
                    .build(),
                ForexPair.builder()
                    .id(FOREX_EUR_USD)
                    .baseCurrency(CurrencyCode.EUR)
                    .quoteCurrency(CurrencyCode.USD)
                    .name("EUR/USD")
                    .exchangeRate(new BigDecimal("1.10"))
                    .liquidity(ForexLiquidity.HIGH)
                    .build()
            );
        forexPairRepository.saveAllAndFlush(prodForexPairs);
        LOGGER.info("Production forex pairs seeded successfully");
    }

    private void seedDevForexPairs() {
        ForexLiquidity[] forexLiquidities = ForexLiquidity.values();
        int i = 0;
        List<ForexPair> forexPairs = new ArrayList<>();
        for (CurrencyCode currencyCode1 : CurrencyCode.values()) {
            for (CurrencyCode currencyCode2 : CurrencyCode.values()) {
                if (currencyCode1.equals(currencyCode2)) {
                    continue;
                }

                forexPairs.add(
                    ForexPair.builder()
                        .baseCurrency(currencyCode1)
                        .quoteCurrency(currencyCode2)
                        .liquidity(forexLiquidities[i++])
                        .name(currencyCode1 + "/" + currencyCode2)
                        .exchangeRate(new BigDecimal("3.5"))
                        .build()
                );
                i = i % 3;
            }
        }
        forexPairRepository.saveAllAndFlush(forexPairs);
        LOGGER.info("forex pairs seeded successfully");
    }

    private void seedProductionFutures() {
        List<Future> prodFutures =
            List.of(
                Future.builder()
                    .id(FUTURE_CRUDE_OIL)
                    .name("Crude Oil")
                    .contractSize(1000)
                    .contractUnit(UnitName.BARREL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(13)
                    )
                    .build()
            );
        futureRepository.saveAllAndFlush(prodFutures);
        LOGGER.info("Production futures seeded successfully");
    }

    private void seedDevFutures() {
        List<Future> devFutures =
            List.of(
                Future.builder()
                    .id(UUID.fromString("74A648B4-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Corn")
                    .contractSize(5000)
                    .contractUnit(UnitName.BUSHEL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusYears(1)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648B5-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Soybean")
                    .contractSize(5000)
                    .contractUnit(UnitName.BUSHEL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusYears(2)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648B8-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Soybean Oil")
                    .contractSize(60000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusYears(1)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648B7-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Soybean Meal")
                    .contractSize(180000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(9)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648BA-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Chicago Wheat")
                    .contractSize(5000)
                    .contractUnit(UnitName.BUSHEL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(10)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648BB-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Live Cattle")
                    .contractSize(40000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(11)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648BC-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Wheat")
                    .contractSize(5000)
                    .contractUnit(UnitName.BUSHEL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(7)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648BD-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Feeder Cattle")
                    .contractSize(50000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(8)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648BE-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Lean Hog")
                    .contractSize(40000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusYears(3)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648BF-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Pork Cutout")
                    .contractSize(40000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(6)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648C0-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Nonfat Dry Milk")
                    .contractSize(44000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(3)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648C1-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Class 3 Milk")
                    .contractSize(200000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(11)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648C2-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Class 4 Milk")
                    .contractSize(200000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(3)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648C3-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Cash-Settled Butter")
                    .contractSize(20000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(9)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648C4-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Cash-Settled Cheese")
                    .contractSize(20000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(7)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648C5-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Block Cheese")
                    .contractSize(2000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(9)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648C6-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Oats")
                    .contractSize(5000)
                    .contractUnit(UnitName.BUSHEL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(4)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648C7-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Rough Rice")
                    .contractSize(180000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(33)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648C8-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Urea")
                    .contractSize(200000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(13)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648C9-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Crude Oil")
                    .contractSize(1000)
                    .contractUnit(UnitName.BARREL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(13)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648CA-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Natural Gas")
                    .contractSize(10000)
                    .contractUnit(UnitName.MMBTU)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(7)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648CB-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Gasoline")
                    .contractSize(42000)
                    .contractUnit(UnitName.GALLON)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(9)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648CC-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("E-Mini Crude Oil")
                    .contractSize(500)
                    .contractUnit(UnitName.BARREL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(12)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648CD-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("NY Harbor ULSD")
                    .contractSize(42000)
                    .contractUnit(UnitName.GALLON)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(10)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648CE-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Micro Crude Oil")
                    .contractSize(100)
                    .contractUnit(UnitName.BARREL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(8)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648CF-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Henry Hub Natural Gas")
                    .contractSize(10000)
                    .contractUnit(UnitName.MMBTU)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(6)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648D0-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Buckeye Jet Fuel")
                    .contractSize(42000)
                    .contractUnit(UnitName.GALLON)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(14)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648D1-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Gold")
                    .contractSize(100)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(15)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648D2-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Silver")
                    .contractSize(5000)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(16)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648D3-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Platinum")
                    .contractSize(50)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(17)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648D4-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Copper")
                    .contractSize(25000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(18)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648D5-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Aluminum")
                    .contractSize(50000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(19)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648D6-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("E-Mini Copper")
                    .contractSize(12500)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(20)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648D7-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Copper Mini")
                    .contractSize(12500)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(21)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648D8-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Silver Mini")
                    .contractSize(1000)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(22)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648D9-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Platinum Mini")
                    .contractSize(10)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(23)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648DA-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Gold Options")
                    .contractSize(100)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(24)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648DB-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Silver Options")
                    .contractSize(5000)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(25)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648DC-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Palladium Options")
                    .contractSize(100)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(26)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648DD-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Cotton")
                    .contractSize(50000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(27)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648DE-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Coffee")
                    .contractSize(37500)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(28)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648DF-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Sugar")
                    .contractSize(112000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(29)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648E0-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Cocoa")
                    .contractSize(10)
                    .contractUnit(UnitName.METRIC_TON)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(30)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648E1-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Orange Juice")
                    .contractSize(15000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(31)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("74A648E2-0DBD-11F0-B4C8-0800200C9A66"))
                    .name("Lumber Options")
                    .contractSize(1100)
                    .contractUnit(UnitName.BOARD_FEET)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(32)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("6AE088E0-0DBF-11F0-B4C8-0800200C9A66"))
                    .name("Lean Hog Options")
                    .contractSize(40000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(33)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("6AE088E1-0DBF-11F0-B4C8-0800200C9A66"))
                    .name("Live Cattle Options")
                    .contractSize(40000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(34)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("6AE088E2-0DBF-11F0-B4C8-0800200C9A66"))
                    .name("Feeder Cattle Options")
                    .contractSize(50000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(35)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("6AE088E3-0DBF-11F0-B4C8-0800200C9A66"))
                    .name("Butter Options")
                    .contractSize(20000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(36)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("6AE088E4-0DBF-11F0-B4C8-0800200C9A66"))
                    .name("Cheese Options")
                    .contractSize(20000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(37)
                    )
                    .build(),
                Future.builder()
                    .id(UUID.fromString("6AE088E5-0DBF-11F0-B4C8-0800200C9A66"))
                    .name("Pork Belly Options")
                    .contractSize(40000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(38)
                    )
                    .build()
            );

        for (Future fut : devFutures) {
            if (!futureRepository.existsById(fut.getId())) {
                futureRepository.saveAndFlush(fut);
            }
        }
    }

    private void seedProductionExchanges() {
        List<Exchange> exchanges =
            List.of(
                Exchange.builder()
                    .id(EXCHANGE_NASDAQ)
                    .exchangeName("Nasdaq")
                    .exchangeAcronym("NASDAQ")
                    .exchangeMICCode("XNAS")
                    .polity("USA")
                    .currency(CurrencyCode.USD)
                    .timeZone("America/New_York")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 30),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(16, 0),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build()
            );
        exchangeRepository.saveAllAndFlush(exchanges);
    }

    private void seedDevExchanges() {
        List<Exchange> exchanges =
            List.of(
                Exchange.builder()
                    .id(UUID.fromString("6AE088E6-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Jakarta Futures Exchange (bursa Berjangka Jakarta)")
                    .exchangeAcronym("BBJ")
                    .exchangeMICCode("XBBJ")
                    .polity("Indonesia")
                    .currency(CurrencyCode.EUR)
                    .timeZone("Asia/Jakarta")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 0),
                            ZoneOffset.of("+07:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(17, 30),
                            ZoneOffset.of("+07:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088E7-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Asx - Trade24")
                    .exchangeAcronym("SFE")
                    .exchangeMICCode("XSFE")
                    .polity("Australia")
                    .currency(CurrencyCode.AUD)
                    .timeZone("Australia/Melbourne")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(10, 0),
                            ZoneOffset.of("+10:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(16, 0),
                            ZoneOffset.of("+10:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088E8-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Cboe Edga U.s. Equities Exchange Dark")
                    .exchangeAcronym("EDGADARK")
                    .exchangeMICCode("EDGD")
                    .polity("United States")
                    .currency(CurrencyCode.USD)
                    .timeZone("America/New_York")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 30),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(16, 0),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088E9-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Clear Street")
                    .exchangeAcronym("CLST")
                    .exchangeMICCode("CLST")
                    .polity("United States")
                    .currency(CurrencyCode.USD)
                    .timeZone("America/New_York")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 30),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(16, 0),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088EA-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Wall Street Access Nyc")
                    .exchangeAcronym("WABR")
                    .exchangeMICCode("WABR")
                    .polity("United States")
                    .currency(CurrencyCode.USD)
                    .timeZone("America/New_York")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 30),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(16, 0),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088EB-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Marex Spectron Europe Limited - Otf")
                    .exchangeAcronym("MSEL OTF")
                    .exchangeMICCode("MSEL")
                    .polity("Ireland")
                    .currency(CurrencyCode.EUR)
                    .timeZone("Europe/Dublin")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(8, 0),
                            ZoneOffset.of("+01:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(16, 30),
                            ZoneOffset.of("+01:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088EC-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Borsa Italiana Equity Mtf")
                    .exchangeAcronym("BITEQMTF")
                    .exchangeMICCode("MTAH")
                    .polity("Italy")
                    .currency(CurrencyCode.EUR)
                    .timeZone("Europe/Rome")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 0),
                            ZoneOffset.of("+02:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(17, 25),
                            ZoneOffset.of("+02:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088ED-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Clearcorp Dealing Systems India Limited - Astroid")
                    .exchangeAcronym("ASTROID")
                    .exchangeMICCode("ASTR")
                    .polity("India")
                    .currency(CurrencyCode.USD)
                    .timeZone("Asia/Kolkata")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 15),
                            ZoneOffset.of("+05:30")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(15, 30),
                            ZoneOffset.of("+05:30")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088EE-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Memx Llc Equities")
                    .exchangeAcronym("MEMX")
                    .exchangeMICCode("MEMX")
                    .polity("United States")
                    .currency(CurrencyCode.USD)
                    .timeZone("America/New_York")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 30),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(16, 0),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088EF-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Natixis - Systematic Internaliser")
                    .exchangeAcronym("NATX")
                    .exchangeMICCode("NATX")
                    .polity("France")
                    .currency(CurrencyCode.EUR)
                    .timeZone("Europe/Paris")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 0),
                            ZoneOffset.of("+02:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(17, 30),
                            ZoneOffset.of("+02:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088F0-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Currenex Ireland Mtf - Rfq")
                    .exchangeAcronym("CNX MTF")
                    .exchangeMICCode("ICXR")
                    .polity("Ireland")
                    .currency(CurrencyCode.EUR)
                    .timeZone("Europe/Dublin")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(8, 0),
                            ZoneOffset.of("+01:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(16, 30),
                            ZoneOffset.of("+01:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088F1-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Neo Exchange - Neo-l (market By Order)")
                    .exchangeAcronym("NEO-L")
                    .exchangeMICCode("NEOE")
                    .polity("Canada")
                    .currency(CurrencyCode.CAD)
                    .timeZone("America/Montreal")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 30),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(16, 0),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088F2-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Polish Trading Point")
                    .exchangeAcronym("PTP")
                    .exchangeMICCode("PTPG")
                    .polity("Poland")
                    .currency(CurrencyCode.EUR)
                    .timeZone("Europe/Warsaw")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 0),
                            ZoneOffset.of("+02:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(17, 35),
                            ZoneOffset.of("+02:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088F3-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Pfts Stock Exchange")
                    .exchangeAcronym("PFTS")
                    .exchangeMICCode("PFTS")
                    .polity("Ukraine")
                    .currency(CurrencyCode.EUR)
                    .timeZone("Europe/Kiev")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(10, 0),
                            ZoneOffset.of("+03:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(17, 30),
                            ZoneOffset.of("+03:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088F4-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Cboe Australia - Transferable Custody Receipt Market")
                    .exchangeAcronym("CHI-X")
                    .exchangeMICCode("CXAR")
                    .polity("Australia")
                    .currency(CurrencyCode.AUD)
                    .timeZone("Australia/Melbourne")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(10, 0),
                            ZoneOffset.of("+10:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(16, 0),
                            ZoneOffset.of("+10:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088F5-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Essex Radez Llc")
                    .exchangeAcronym("GLPS")
                    .exchangeMICCode("GLPS")
                    .polity("United States")
                    .currency(CurrencyCode.USD)
                    .timeZone("America/New_York")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 30),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(16, 0),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088F6-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("London Metal Exchange")
                    .exchangeAcronym("LME")
                    .exchangeMICCode("XLME")
                    .polity("United Kingdom")
                    .currency(CurrencyCode.EUR)
                    .timeZone("Europe/London")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(8, 0),
                            ZoneOffset.of("+01:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(16, 0),
                            ZoneOffset.of("+01:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088F7-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Multi Commodity Exchange Of India Ltd.")
                    .exchangeAcronym("MCX")
                    .exchangeMICCode("XIMC")
                    .polity("India")
                    .currency(CurrencyCode.JPY)
                    .timeZone("Asia/Kolkata")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 15),
                            ZoneOffset.of("+05:30")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(15, 30),
                            ZoneOffset.of("+05:30")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088F8-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName(
                        "Cassa Di Compensazione E Garanzia Spa - Ccp Agricultural Commodity Derivatives"
                    )
                    .exchangeAcronym("CCGAGRIDER")
                    .exchangeMICCode("CGGD")
                    .polity("Italy")
                    .currency(CurrencyCode.EUR)
                    .timeZone("Europe/Rome")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 0),
                            ZoneOffset.of("+02:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(17, 25),
                            ZoneOffset.of("+02:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build(),
                Exchange.builder()
                    .id(UUID.fromString("6AE088F9-0DBF-11F0-B4C8-0800200C9A66"))
                    .exchangeName("Toronto Stock Exchange - Drk")
                    .exchangeAcronym("TSX DRK")
                    .exchangeMICCode("XDRK")
                    .polity("Canada")
                    .currency(CurrencyCode.CAD)
                    .timeZone("America/Montreal")
                    .openTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(9, 30),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .closeTime(
                        OffsetDateTime.of(
                            LocalDate.now(),
                            LocalTime.of(16, 0),
                            ZoneOffset.of("-04:00")
                        )
                    )
                    .createdAt(LocalDate.now())
                    .build()
            );

        for (Exchange devExchange : exchanges) {
            if (!exchangeRepository.existsById(devExchange.getId())) {
                exchangeRepository.saveAndFlush(devExchange);
            }
        }
    }
}
