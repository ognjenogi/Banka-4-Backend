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
    private static final UUID STOCK_GOOGLE =
        UUID.fromString("878A16EA-6A07-45FA-B016-3DF9B3CDA497");
    private static final UUID STOCK_META = UUID.fromString("6B3E38E9-3302-4176-BCF4-84C5E0FA7056");
    private static final UUID STOCK_BERKSHIRE =
        UUID.fromString("A10986D7-D683-40CA-8D0E-54D6872DA1C6");
    private static final UUID STOCK_JJ = UUID.fromString("05624974-1AC7-4BFC-BBA2-90E7250F6D97");
    private static final UUID STOCK_JP_MORGAN =
        UUID.fromString("D473C33B-3008-4D09-B0C1-4BA653E0ACAC");
    private static final UUID STOCK_VISA = UUID.fromString("8965FCC7-798C-4CCC-8154-8493599603BF");
    private static final UUID STOCK_PROCTER_GAMBLE =
        UUID.fromString("6CA9AF13-09BC-42A0-83F6-BE2DB67F5743");
    private static final UUID STOCK_UNITED_HEALTH =
        UUID.fromString("2C45DBEA-7FBA-4F7C-91F4-8731438113EA");
    private static final UUID STOCK_EXXON_MOBIL =
        UUID.fromString("5A1EB0FC-2ECD-402A-A073-399F94DF6BA3");
    private static final UUID STOCK_WALMART =
        UUID.fromString("8303F25B-1414-463B-9687-9FD5AE3A76A7");
    private static final UUID STOCK_PFIZER =
        UUID.fromString("46430DE4-0BC9-4D65-90A4-A2CF571EC106");
    private static final UUID STOCK_CHEVRON =
        UUID.fromString("4BE415AB-E5B7-4273-A915-711C2BA8E3E6");
    private static final UUID STOCK_COCA_COLA =
        UUID.fromString("E33FF9E4-1592-44F6-BCB2-543FA73C6E7B");
    private static final UUID STOCK_DISNEY =
        UUID.fromString("8FFE91C4-D2C1-45CD-9993-7EB2C6D98A9A");
    private static final UUID STOCK_PEPSICO =
        UUID.fromString("551AA271-D847-4D00-A02B-5ED6038EE8E9");
    private static final UUID STOCK_INTEL = UUID.fromString("D98BCBE3-4CC9-4DF1-A843-211946C43CF6");

    private static final UUID FOREX_USD_EUR =
        UUID.fromString("95F5A021-6A9F-4622-ADEF-10572CE83974");
    private static final UUID FOREX_EUR_USD =
        UUID.fromString("44D70B7F-A601-49BA-B279-83B8186D3EC5");

    private static final UUID FUTURE_CRUDE_OIL =
        UUID.fromString("43869046-B106-4AF0-902E-5E70F2668C41");
    private static final UUID FUTURE_CORN = UUID.fromString("20FEC473-54EA-40DA-9873-7E7AA4EDECF4");
    private static final UUID FUTURE_SOYBEAN =
        UUID.fromString("3B8C2450-A3C4-4CFC-8A39-CDAE99A39D30");
    private static final UUID FUTURE_SOYBEAN_OIL =
        UUID.fromString("16D2D1A7-5ABF-43C5-B5ED-002C9C9B1D8A");
    private static final UUID FUTURE_SOYBEAN_MEAL =
        UUID.fromString("C41A1EF3-05EA-48C0-9176-6BB67E699B7D");
    private static final UUID FUTURE_CHICAGO_WHEAT =
        UUID.fromString("6FC2E487-1424-49C3-A791-EE3AF8ED6DC3");
    private static final UUID FUTURE_LIVE_CATTLE =
        UUID.fromString("19B146A0-36AD-4965-8B2A-841326C53B4C");
    private static final UUID FUTURE_WHEAT =
        UUID.fromString("32E4475A-55AB-4BD5-958B-DE4D7CAD4F28");
    private static final UUID FUTURE_FEEDER_CATTLE =
        UUID.fromString("C3EF8DF8-5329-4E14-A26C-A7538B0B0EAF");
    private static final UUID FUTURE_LEAN_HOG =
        UUID.fromString("EBF2A869-7FD3-4DC2-BEDD-700C91AB05E5");
    private static final UUID FUTURE_PORK_CUTOUT =
        UUID.fromString("DC9D7A84-E6BE-43BC-ADD6-9B9CBFC473A3");
    private static final UUID FUTURE_NONFAT_DRY_MILK =
        UUID.fromString("92CEB433-2094-4CFE-826D-2EA9A664E8C0");
    private static final UUID FUTURE_CLASS3_MILK =
        UUID.fromString("F9620374-16D3-4C54-A8F2-BB4A6EEC9764");
    private static final UUID FUTURE_CLASS4_MILK =
        UUID.fromString("A20DCD14-3A47-4CFF-BF7D-A9FE0DF0C4B2");
    private static final UUID FUTURE_CASH_SETTLED_BUTTER =
        UUID.fromString("FC9264AD-7E8D-4CBB-8528-7419351B9007");
    private static final UUID FUTURE_CASH_SETTLED_CHEESE =
        UUID.fromString("75D72EDA-7D48-4A61-BF62-B9C79F0A3A4F");
    private static final UUID FUTURE_BLOCK_CHEESE =
        UUID.fromString("43594257-92A4-497E-A72C-347FE538C4D4");
    private static final UUID FUTURE_OATS = UUID.fromString("0DED5D6E-B860-4D60-AD96-31194C7D3418");
    private static final UUID FUTURE_ROUGH_RICE =
        UUID.fromString("2080F453-2EED-4FAC-B64A-8AF1938EAF8B");
    private static final UUID FUTURE_UREA = UUID.fromString("3EFEC67E-7F41-4D31-8AD0-B25918BCEE7F");
    private static final UUID FUTURE_NATURAL_GAS =
        UUID.fromString("C1311DB1-FAB0-4C0B-B360-ECF96FBA97AD");
    private static final UUID FUTURE_GASOLINE =
        UUID.fromString("81AEA6EA-1AFC-4F22-A4F4-AEA7B7D818BB");
    private static final UUID FUTURE_EMINI_CRUDE_OIL =
        UUID.fromString("D15714EA-48B4-4616-97AC-9BE1E1A14334");
    private static final UUID FUTURE_NY_HARBOUR_ULSD =
        UUID.fromString("63DFBA0F-C957-4057-A272-72B8EB1DB6FC");
    private static final UUID FUTURE_MICRO_CRUDE_OIL =
        UUID.fromString("93AB664A-271B-478F-8028-308C2D849BED");
    private static final UUID FUTURE_HENRY_HUB_NATURAL_GAS =
        UUID.fromString("0890EE66-663A-46D9-BF9D-CA017D8EC569");
    private static final UUID FUTURE_BUCKEYE_JET_FUEL =
        UUID.fromString("279A8812-B576-4644-8852-3EFB0083B2F3");
    private static final UUID FUTURE_GOLD = UUID.fromString("B5874D27-1B2F-4BA6-BBDF-9870B2FCAB34");
    private static final UUID FUTURE_SILVER =
        UUID.fromString("D0664758-019F-436F-950B-D948BFF97ECD");
    private static final UUID FUTURE_PLATINUM =
        UUID.fromString("CCF559D9-8ED8-481D-94B6-651005782ECC");
    private static final UUID FUTURE_COPPER =
        UUID.fromString("0004923D-6E9D-4398-8532-87FE294E22F0");
    private static final UUID FUTURE_ALUMINUM =
        UUID.fromString("2B995BCE-77A7-43AE-8399-96B2154DBD03");
    private static final UUID FUTURE_EMINI_COPPER =
        UUID.fromString("01C10A5F-D9B9-4417-86A2-A9C5B191659A");
    private static final UUID FUTURE_COPPER_MINI =
        UUID.fromString("AE9B921D-9E40-4A3E-BDA5-4359E601535D");
    private static final UUID FUTURE_SILVER_MINI =
        UUID.fromString("107AC6BC-B38B-4246-919D-9DB93279C8D4");
    private static final UUID FUTURE_PLATINUM_MINI =
        UUID.fromString("434F91BF-542F-4F93-8A35-4083E2F0FC3A");
    private static final UUID FUTURE_GOLD_OPTIONS =
        UUID.fromString("3B3FAF1D-3F81-4C8D-94B2-F99A75B46D28");
    private static final UUID FUTURE_SILVER_OPTIONS =
        UUID.fromString("153366F3-1397-495E-968B-8EE64E6DEC76");
    private static final UUID FUTURE_PALLADIUM_OPTIONS =
        UUID.fromString("AA883F29-1171-45F6-ADF7-0980A12BDC2C");
    private static final UUID FUTURE_COTTON =
        UUID.fromString("D914C480-B9F3-45E2-8340-893F54FA7313");
    private static final UUID FUTURE_COFFEE =
        UUID.fromString("885AF525-3B58-40C4-914E-6E618414F59A");
    private static final UUID FUTURE_SUGAR =
        UUID.fromString("547B74A2-F0B6-4A15-91B3-88BEBA09F9B9");
    private static final UUID FUTURE_COCOA =
        UUID.fromString("EA90E1D7-4CB2-4807-A798-9A512249A41B");
    private static final UUID FUTURE_ORANGE_JUICE =
        UUID.fromString("1592AB64-4C8F-4D41-AE6B-FF4C54C7C852");
    private static final UUID FUTURE_LUMBER_OPTIONS =
        UUID.fromString("4D6D2564-6AE9-4E17-8F27-2E82BA507CCE");
    private static final UUID FUTURE_LEAN_HOG_OPTIONS =
        UUID.fromString("EF064B75-AAC9-4ED1-AF2E-B0825D1EF730");
    private static final UUID FUTURE_LIVE_CATTLE_OPTIONS =
        UUID.fromString("2029234A-FC9D-42D1-BDFA-248674DE1174");
    private static final UUID FUTURE_CHEESE_OPTIONS =
        UUID.fromString("E333D933-EE5F-4B0E-B15F-0A8694DE55E3");
    private static final UUID FUTURE_FEEDER_CATTLE_OPTIONS =
        UUID.fromString("6CCB9F13-3389-4024-A35E-E047EC0A4454");
    private static final UUID FUTURE_BUTTER_OPTIONS =
        UUID.fromString("7A81ACC1-54EE-4430-BADD-6FEC5A728851");
    private static final UUID FUTURE_PORK_BELLY_OPTIONS =
        UUID.fromString("D6E870EE-4578-454B-A811-E2D812D1852C");


    private static final UUID EXCHANGE_NASDAQ =
        UUID.fromString("E3A645C4-68CE-4DE3-A597-2C55FA4E4E78");
    private static final UUID EXCHANGE_JAKARTA_FUTURES =
        UUID.fromString("9441EF4F-E730-4F83-ABBE-3385EC82B69F");
    private static final UUID EXCHANGE_ASX_TRADE24 =
        UUID.fromString("09C3D530-DAEF-46D4-A94E-C8C2BAB8986D");
    private static final UUID EXCHANGE_CBOE_EDGA =
        UUID.fromString("2DF30F00-2A4A-473D-A967-41613B1498D5");
    private static final UUID EXCHANGE_CLEAR_STREET =
        UUID.fromString("4A97DE01-C9D7-4BA7-89E3-77AE7DD43198");
    private static final UUID EXCHANGE_WALLSTREET_ACCESS =
        UUID.fromString("2FF4D514-EA55-4F9C-A445-D3CCEF827B2C");
    private static final UUID EXCHANGE_MAREX_SPECTRON =
        UUID.fromString("FFC67C7F-1E83-42A7-AB57-46555466FB82");
    private static final UUID EXCHANGE_BORSA_ITALIANA =
        UUID.fromString("18F3773B-E99F-41BE-9595-7A45AAEE3756");
    private static final UUID EXCHANGE_CLEARCORP_DEALING =
        UUID.fromString("22347B09-298C-454E-9121-3472C54327AA");
    private static final UUID EXCHANGE_MEMX_LLC =
        UUID.fromString("25E98506-A01D-4869-87FD-71FC168E1346");
    private static final UUID EXCHANGE_NATIXIS =
        UUID.fromString("065E07A0-7983-4BE3-AD99-9EEBCB91B82B");
    private static final UUID EXCHANGE_CURRENEX_IRELAND =
        UUID.fromString("D4C15F93-3B0C-4D55-9F55-B26905572F2A");
    private static final UUID EXCHANGE_NEO_EXCHANGE =
        UUID.fromString("3AA1EFC9-A804-4B80-86F9-B2D7474B5CB3");
    private static final UUID EXCHANGE_POLISH_TRADING_POINT =
        UUID.fromString("5BD52C19-1B9C-4495-A662-32BF303D02F1");
    private static final UUID EXCHANGE_PFTS_STOCK_EXCHANGE =
        UUID.fromString("C9440401-A7F3-4DC4-ADB6-483726B1AD8F");
    private static final UUID EXCHANGE_CBOE_AUSTRALIA =
        UUID.fromString("0E422290-469C-4EC6-82DD-37414F15FE41");
    private static final UUID EXCHANGE_ESSEX_RADEZ =
        UUID.fromString("D9790530-97BD-4D5A-9756-CB776B82C757");
    private static final UUID EXCHANGE_LONDON_METAL =
        UUID.fromString("F91DEF89-7BC4-4E67-B190-D7F5E02C2E74");
    private static final UUID EXCHANGE_MULTICOMMODITY_EXCHANGE =
        UUID.fromString("C47FF2FA-0994-4BA7-94CF-E89D533F9966");
    private static final UUID EXCHANGE_CASSA_DI_COMPENSAZIONE =
        UUID.fromString("798D5FBE-82B5-4D52-AFF2-0DD33304FEDF");
    private static final UUID EXCHANGE_TORONTO_STOCK_EXCHANGE =
        UUID.fromString("3FED4834-BC31-408C-9F4B-C5A1D3B0EF27");

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
        List<Stock> devStocks =
            List.of(
                Stock.builder()
                    .id(STOCK_GOOGLE)
                    .name("Google (Alphabet)")
                    .outstandingShares(600000000)
                    .dividendYield(new BigDecimal("0"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_META)
                    .name("Meta (Facebook)")
                    .outstandingShares(2700000000L)
                    .dividendYield(new BigDecimal("0"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_BERKSHIRE)
                    .name("Berkshire Hathaway")
                    .outstandingShares(2200000000L)
                    .dividendYield(new BigDecimal("0"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_JJ)
                    .name("Johnson & Johnson")
                    .outstandingShares(2600000000L)
                    .dividendYield(new BigDecimal("0.025"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_JP_MORGAN)
                    .name("JPMorgan Chase")
                    .outstandingShares(2900000000L)
                    .dividendYield(new BigDecimal("0.029"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_VISA)
                    .name("Visa")
                    .outstandingShares(2200000000L)
                    .dividendYield(new BigDecimal("0.008"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_PROCTER_GAMBLE)
                    .name("Procter & Gamble")
                    .outstandingShares(2500000000L)
                    .dividendYield(new BigDecimal("0.026"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_UNITED_HEALTH)
                    .name("UnitedHealth Group")
                    .outstandingShares(900000000)
                    .dividendYield(new BigDecimal("0.016"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_EXXON_MOBIL)
                    .name("ExxonMobil")
                    .outstandingShares(4000000000L)
                    .dividendYield(new BigDecimal("0.031"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_WALMART)
                    .name("Walmart")
                    .outstandingShares(2800000000L)
                    .dividendYield(new BigDecimal("0.014"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_PFIZER)
                    .name("Pfizer")
                    .outstandingShares(5600000000L)
                    .dividendYield(new BigDecimal("0.046"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_CHEVRON)
                    .name("Chevron")
                    .outstandingShares(1900000000L)
                    .dividendYield(new BigDecimal("0.041"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_COCA_COLA)
                    .name("Coca-Cola")
                    .outstandingShares(4300000000L)
                    .dividendYield(new BigDecimal("0.029"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_DISNEY)
                    .name("Disney")
                    .outstandingShares(1800000000L)
                    .dividendYield(new BigDecimal("0"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_PEPSICO)
                    .name("PepsiCo")
                    .outstandingShares(1400000000L)
                    .dividendYield(new BigDecimal("0.028"))
                    .createdAt(OffsetDateTime.now())
                    .build(),
                Stock.builder()
                    .id(STOCK_INTEL)
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
                    .id(FUTURE_CORN)
                    .name("Corn")
                    .contractSize(5000)
                    .contractUnit(UnitName.BUSHEL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusYears(1)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_SOYBEAN)
                    .name("Soybean")
                    .contractSize(5000)
                    .contractUnit(UnitName.BUSHEL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusYears(2)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_SOYBEAN_OIL)
                    .name("Soybean Oil")
                    .contractSize(60000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusYears(1)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_SOYBEAN_MEAL)
                    .name("Soybean Meal")
                    .contractSize(180000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(9)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_CHICAGO_WHEAT)
                    .name("Chicago Wheat")
                    .contractSize(5000)
                    .contractUnit(UnitName.BUSHEL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(10)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_LIVE_CATTLE)
                    .name("Live Cattle")
                    .contractSize(40000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(11)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_WHEAT)
                    .name("Wheat")
                    .contractSize(5000)
                    .contractUnit(UnitName.BUSHEL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(7)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_FEEDER_CATTLE)
                    .name("Feeder Cattle")
                    .contractSize(50000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(8)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_LEAN_HOG)
                    .name("Lean Hog")
                    .contractSize(40000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusYears(3)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_PORK_CUTOUT)
                    .name("Pork Cutout")
                    .contractSize(40000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(6)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_NONFAT_DRY_MILK)
                    .name("Nonfat Dry Milk")
                    .contractSize(44000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(3)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_CLASS3_MILK)
                    .name("Class 3 Milk")
                    .contractSize(200000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(11)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_CLASS4_MILK)
                    .name("Class 4 Milk")
                    .contractSize(200000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(3)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_CASH_SETTLED_BUTTER)
                    .name("Cash-Settled Butter")
                    .contractSize(20000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(9)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_CASH_SETTLED_CHEESE)
                    .name("Cash-Settled Cheese")
                    .contractSize(20000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(7)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_BLOCK_CHEESE)
                    .name("Block Cheese")
                    .contractSize(2000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(9)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_OATS)
                    .name("Oats")
                    .contractSize(5000)
                    .contractUnit(UnitName.BUSHEL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(4)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_ROUGH_RICE)
                    .name("Rough Rice")
                    .contractSize(180000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(33)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_UREA)
                    .name("Urea")
                    .contractSize(200000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(13)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_EMINI_CRUDE_OIL)
                    .name("Crude Oil")
                    .contractSize(1000)
                    .contractUnit(UnitName.BARREL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(13)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_NATURAL_GAS)
                    .name("Natural Gas")
                    .contractSize(10000)
                    .contractUnit(UnitName.MMBTU)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(7)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_GASOLINE)
                    .name("Gasoline")
                    .contractSize(42000)
                    .contractUnit(UnitName.GALLON)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(9)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_EMINI_CRUDE_OIL)
                    .name("E-Mini Crude Oil")
                    .contractSize(500)
                    .contractUnit(UnitName.BARREL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(12)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_NY_HARBOUR_ULSD)
                    .name("NY Harbor ULSD")
                    .contractSize(42000)
                    .contractUnit(UnitName.GALLON)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(10)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_MICRO_CRUDE_OIL)
                    .name("Micro Crude Oil")
                    .contractSize(100)
                    .contractUnit(UnitName.BARREL)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(8)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_HENRY_HUB_NATURAL_GAS)
                    .name("Henry Hub Natural Gas")
                    .contractSize(10000)
                    .contractUnit(UnitName.MMBTU)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(6)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_BUCKEYE_JET_FUEL)
                    .name("Buckeye Jet Fuel")
                    .contractSize(42000)
                    .contractUnit(UnitName.GALLON)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(14)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_GOLD)
                    .name("Gold")
                    .contractSize(100)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(15)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_SILVER)
                    .name("Silver")
                    .contractSize(5000)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(16)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_PLATINUM)
                    .name("Platinum")
                    .contractSize(50)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(17)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_COPPER)
                    .name("Copper")
                    .contractSize(25000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(18)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_ALUMINUM)
                    .name("Aluminum")
                    .contractSize(50000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(19)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_EMINI_COPPER)
                    .name("E-Mini Copper")
                    .contractSize(12500)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(20)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_COPPER_MINI)
                    .name("Copper Mini")
                    .contractSize(12500)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(21)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_SILVER_MINI)
                    .name("Silver Mini")
                    .contractSize(1000)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(22)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_PLATINUM_MINI)
                    .name("Platinum Mini")
                    .contractSize(10)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(23)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_GOLD_OPTIONS)
                    .name("Gold Options")
                    .contractSize(100)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(24)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_SILVER_OPTIONS)
                    .name("Silver Options")
                    .contractSize(5000)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(25)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_PALLADIUM_OPTIONS)
                    .name("Palladium Options")
                    .contractSize(100)
                    .contractUnit(UnitName.TROY_OUNCE)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(26)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_COTTON)
                    .name("Cotton")
                    .contractSize(50000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(27)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_COFFEE)
                    .name("Coffee")
                    .contractSize(37500)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(28)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_SUGAR)
                    .name("Sugar")
                    .contractSize(112000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(29)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_COCOA)
                    .name("Cocoa")
                    .contractSize(10)
                    .contractUnit(UnitName.METRIC_TON)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(30)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_ORANGE_JUICE)
                    .name("Orange Juice")
                    .contractSize(15000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(31)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_LUMBER_OPTIONS)
                    .name("Lumber Options")
                    .contractSize(1100)
                    .contractUnit(UnitName.BOARD_FEET)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(32)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_LEAN_HOG_OPTIONS)
                    .name("Lean Hog Options")
                    .contractSize(40000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(33)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_LIVE_CATTLE_OPTIONS)
                    .name("Live Cattle Options")
                    .contractSize(40000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(34)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_FEEDER_CATTLE_OPTIONS)
                    .name("Feeder Cattle Options")
                    .contractSize(50000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(35)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_BUTTER_OPTIONS)
                    .name("Butter Options")
                    .contractSize(20000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(36)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_CHEESE_OPTIONS)
                    .name("Cheese Options")
                    .contractSize(20000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(37)
                    )
                    .build(),
                Future.builder()
                    .id(FUTURE_PORK_BELLY_OPTIONS)
                    .name("Pork Belly Options")
                    .contractSize(40000)
                    .contractUnit(UnitName.POUND)
                    .settlementDate(
                        OffsetDateTime.now()
                            .plusMonths(38)
                    )
                    .build()
            );
        futureRepository.saveAllAndFlush(devFutures);
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
                    .id(EXCHANGE_JAKARTA_FUTURES)
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
                    .id(EXCHANGE_ASX_TRADE24)
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
                    .id(EXCHANGE_CBOE_EDGA)
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
                    .id(EXCHANGE_CLEAR_STREET)
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
                    .id(EXCHANGE_WALLSTREET_ACCESS)
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
                    .id(EXCHANGE_MAREX_SPECTRON)
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
                    .id(EXCHANGE_BORSA_ITALIANA)
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
                    .id(EXCHANGE_CLEARCORP_DEALING)
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
                    .id(EXCHANGE_MEMX_LLC)
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
                    .id(EXCHANGE_NATIXIS)
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
                    .id(EXCHANGE_CURRENEX_IRELAND)
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
                    .id(EXCHANGE_NEO_EXCHANGE)
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
                    .id(EXCHANGE_POLISH_TRADING_POINT)
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
                    .id(EXCHANGE_PFTS_STOCK_EXCHANGE)
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
                    .id(EXCHANGE_CBOE_AUSTRALIA)
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
                    .id(EXCHANGE_ESSEX_RADEZ)
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
                    .id(EXCHANGE_LONDON_METAL)
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
                    .id(EXCHANGE_MULTICOMMODITY_EXCHANGE)
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
                    .id(EXCHANGE_CASSA_DI_COMPENSAZIONE)
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
                    .id(EXCHANGE_TORONTO_STOCK_EXCHANGE)
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

        exchangeRepository.saveAllAndFlush(exchanges);
    }
}
