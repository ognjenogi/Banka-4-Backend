package rs.banka4.stock_service.runners;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Response;
import rs.banka4.rafeisen.common.currency.CurrencyCode;
import rs.banka4.stock_service.config.retrofit.AlphaVantageService;
import rs.banka4.stock_service.domain.exchanges.db.Exchange;
import rs.banka4.stock_service.domain.listing.db.Listing;
import rs.banka4.stock_service.domain.listing.db.ListingDailyPriceInfo;
import rs.banka4.stock_service.domain.listing.dtos.ListingApiDto;
import rs.banka4.stock_service.domain.security.forex.db.CurrencyMapper;
import rs.banka4.stock_service.domain.security.future.db.Future;
import rs.banka4.stock_service.domain.security.future.db.UnitName;
import rs.banka4.stock_service.domain.security.stock.db.Stock;
import rs.banka4.stock_service.domain.security.stock.dtos.StockInfoDto;
import rs.banka4.stock_service.repositories.*;
import rs.banka4.stock_service.utils.ListingsAndOptionsUpdatesScheduler;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class TestDataRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestDataRunner.class);
    public static volatile boolean finishedSeeding = false;

    private final Environment environment;
    private final ForexRepository forexPairRepository;
    private final StockRepository stockRepository;
    private final FutureRepository futureRepository;
    private final ListingRepository listingRepository;
    private final ExchangeRepository exchangeRepository;
    private final ListingDailyPriceInfoRepository listingDailyPriceInfoRepository;
    private final OptionsRepository optionsRepository;
    private final AlphaVantageService alphaRetrofit;

    private final ListingsAndOptionsUpdatesScheduler listingsAndOptionsUpdatesScheduler;
    // This will be used to seed for the first time instead of waiting for an update which can take
    // a long time

    @Value("${alphavantage.api-key}")
    private String vantageKey;

    @Override
    public void run(String... args) {
        runProd();
    }

    public void logRepositoryCounts() {
        LOGGER.info("ForexPairs count: {}", forexPairRepository.count());
        LOGGER.info("Stocks count: {}", stockRepository.count());
        LOGGER.info("Futures count: {}", futureRepository.count());
        LOGGER.info("Listings count: {}", listingRepository.count());
        LOGGER.info("Exchanges count: {}", exchangeRepository.count());
        LOGGER.info("ListingDailyPriceInfos count: {}", listingDailyPriceInfoRepository.count());
        LOGGER.info("Options count: {}", optionsRepository.count());
    }

    @Transactional
    public void runProd() {
        // TODO propagate errors so transaction can fail
        LOGGER.info("Seeding prod");

        if (stockRepository.count() == 0) {
            seedProductionStocks();
        } else {
            LOGGER.info("Not reseeding stockRepository, data already exists");
        }

        if (forexPairRepository.count() == 0) {
            seedProductionForexPairs();
        } else {
            LOGGER.info("Not reseeding forexPairRepository, data already exists");

        }

        if (futureRepository.count() == 0) {
            seedProductionFutures();
        } else {
            LOGGER.info("Not reseeding futureRepository, data already exists");
        }

        if (exchangeRepository.count() == 0) {
            seedProductionExchanges();
        } else {
            LOGGER.info("Not reseeding exchangeRepository, data already exists");
        }

        if (listingRepository.count() == 0) {
            seedProductionListings();
        } else {
            LOGGER.info("Not reseeding listingsRepository, data already exists");
        }

        if (optionsRepository.count() == 0) {
            seedProductionOptions();
        } else {
            LOGGER.info("Not reseeding options, data already exists");
        }

        if (listingDailyPriceInfoRepository.count() == 0) {
            seedProductionListingDailyPriceInfo();
        } else {
            LOGGER.info("Not reseeding listing daily price info, data already exists");
        }

        logRepositoryCounts();
        LOGGER.info("Seeding prod finished, starting stock service...");
        finishedSeeding = true;
    }

    private void seedProductionListingDailyPriceInfo() {
        try {
            List<ListingDailyPriceInfo> listingDailyPriceInfos = new ArrayList<>();
            BigDecimal change = BigDecimal.ZERO;

            for (Listing listing : listingRepository.findAllActiveListings()) {
                change = change.add(BigDecimal.valueOf(0.25));
                if (change.compareTo(BigDecimal.valueOf(1.0)) > 0) {
                    change = BigDecimal.valueOf(-1.0);
                }

                BigDecimal baseBid = listing.getBid();
                BigDecimal baseAsk = listing.getAsk();
                BigDecimal bid = baseBid.subtract(change);
                BigDecimal ask = baseAsk.subtract(change);
                BigDecimal lastPrice =
                    ask.add(bid)
                        .divide(BigDecimal.valueOf(2), 6, RoundingMode.HALF_UP);

                ListingDailyPriceInfo info =
                    ListingDailyPriceInfo.builder()
                        .security(listing.getSecurity())
                        .exchange(listing.getExchange())
                        .date(
                            OffsetDateTime.of(
                                LocalDate.of(
                                    OffsetDateTime.now()
                                        .getYear(),
                                    4,
                                    1
                                ),
                                LocalTime.MIDNIGHT,
                                ZoneOffset.UTC
                            )
                        )
                        .lastPrice(lastPrice)
                        .askHigh(ask)
                        .bigLow(bid)
                        .change(change)
                        .volume(1000)
                        .build();

                listingDailyPriceInfos.add(info);
            }

            listingDailyPriceInfoRepository.saveAllAndFlush(listingDailyPriceInfos);
            LOGGER.info("Production ListingDailyPriceInfo seeded successfully.");
        } catch (Exception e) {
            LOGGER.error(
                "Error occurred while seeding ListingDailyPriceInfo: {}",
                e.getMessage(),
                e
            );
        }
    }

    private void seedProductionOptions() {
        try {
            listingsAndOptionsUpdatesScheduler.updateOptions();
            LOGGER.info("Production options seeded successfully.");
        } catch (Exception e) {
            LOGGER.error("Error occurred while seeding prod options: {}", e.getMessage());
        }
    }

    private Listing fetchListingInfo(
        String ticker,
        long id,
        Stock stock,
        String exchangeAcronym,
        Map<String, Exchange> exchangesMap
    ) throws IOException {
        // https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=AAPL&apikey=7P4ANAS869M38S3B
        // uses same api for daily listings history

        retrofit2.Call<ListingApiDto> call =
            alphaRetrofit.getListingInfo("GLOBAL_QUOTE", ticker, vantageKey);

        ListingApiDto listingApiDto =
            call.execute()
                .body();


        double price = 0.0;
        long fakeContractSize = 0L;
        try {
            price =
                Double.parseDouble(
                    listingApiDto.globalQuoteDto()
                        .priceStr()
                );
            fakeContractSize =
                Long.parseLong(
                    listingApiDto.globalQuoteDto()
                        .volumeStr()
                ) / 1000L;
            fakeContractSize = Long.max(1L, fakeContractSize);
        } catch (Exception e) {
            LOGGER.error("{}", e.getMessage());
        }

        double fakeBid = price * 0.993;
        double fakeAsk = price * 1.007;
        /*
         * System.out.println("----------------"); System.out.println(exchangesMap.get(stock));
         * System.out.println(stock); System.out.println("--------------");
         */
        return Listing.builder()
            .id(new UUID(0L, id))
            .ask(new BigDecimal(fakeAsk))
            .bid(new BigDecimal(fakeBid))
            .contractSize((int) fakeContractSize)
            .active(true)
            .lastRefresh(OffsetDateTime.now())
            .exchange(exchangesMap.get(exchangeAcronym))
            .security(stock)
            .build();
    }

    Map<String, Exchange> makeExchangeMap() {
        Map<String, Exchange> exchangesMap = new HashMap<>();
        for (Exchange exchange : exchangeRepository.findAll()) {
            exchangesMap.put(exchange.getExchangeAcronym(), exchange);
        }
        return exchangesMap;
    }

    Map<String, Stock> makeStockMap() {
        Map<String, Stock> stocksMap = new HashMap<>();
        for (Stock stock : stockRepository.findAll()) {
            stocksMap.put(stock.getTicker(), stock);
        }
        return stocksMap;
    }

    private void seedProductionListings() {
        try {
            listingsAndOptionsUpdatesScheduler.updateListings();
            LOGGER.info("Production listings seeded successfully.");
        } catch (Exception e) {
            LOGGER.error("Error occurred while seeding prod listings: {}", e.getMessage());
        }
    }

    private Stock fetchStockInfo(String ticker, long id) throws IOException {
        // https://www.alphavantage.co/query?function=OVERVIEW&symbol=IBM&apikey=demo

        retrofit2.Call<StockInfoDto> call =
            alphaRetrofit.getStockInfo("OVERVIEW", ticker, vantageKey);

        Response<StockInfoDto> stockInfoDtoResponse = call.execute();
        StockInfoDto stockInfoDto = stockInfoDtoResponse.body();

        BigDecimal divYield = new BigDecimal(0.0);
        try {
            divYield = new BigDecimal(stockInfoDto.dividendYield());
        } catch (Exception e) {
        }

        long outstandingSharesLong;
        try {
            outstandingSharesLong = Long.parseLong(stockInfoDto.outstandingShares());
        } catch (Exception e) {
            return null;
        }

        BigDecimal marketCap = null;
        try {
            marketCap = new BigDecimal(stockInfoDto.marketCap());
        } catch (Exception e) {
        }

        return Stock.builder()
            .id(new UUID(0L, id))
            .name(stockInfoDto.name())
            .dividendYield(divYield)
            .outstandingShares(outstandingSharesLong)
            .createdAt(OffsetDateTime.now())
            .marketCap(marketCap)
            .ticker(ticker)
            .build();
    }

    private void seedProductionStocks() {
        try {
            List<Stock> stocks = new ArrayList<>();
            List<String> tickers = new ArrayList<>();
            InputStream is = getClass().getResourceAsStream("ticker_exchange.csv");
            if (is == null) {
                throw new IllegalArgumentException("File not found!");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String t = line.split(",")[0].strip();
                tickers.add(t);
            }

            long id = 545353;
            for (String ticker : tickers) {
                Stock stock = fetchStockInfo(ticker, id++);
                if (stock == null) {
                    continue;
                }
                stocks.add(stock);
                Thread.sleep(200);
            }
            stockRepository.saveAllAndFlush(stocks);
            LOGGER.info("Production stocks seeded successfully.");
        } catch (Exception e) {
            LOGGER.error("Error occurred while seeding prod stocks: {}", e.getMessage());
        }
    }

    private void seedProductionForexPairs() {
        try {
            listingsAndOptionsUpdatesScheduler.refreshForexPairs();
            LOGGER.info("Production forex pairs seeded successfully.");
        } catch (Exception e) {
            LOGGER.error("Error occurred while seeding prod forex pairs: {}", e.getMessage());
        }
    }

    private String makeFakeTickerForFuture(String name, OffsetDateTime settlementDate) {
        String[] letters = {
            "F","G","H","J","K","M","N","Q","U","V","X","Z"
        };
        String base = getBaseTicker(name);

        StringBuilder sb = new StringBuilder();
        String monthCode = letters[settlementDate.getMonthValue() - 1];
        int year = settlementDate.getYear() % 100;
        sb.append(base);
        sb.append(monthCode);
        sb.append(year);
        return sb.toString()
            .toUpperCase();
    }

    private static String getBaseTicker(String name) {
        String[] words = name.split("[-_ ]");

        String base;
        if (words.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (String word : words) {
                if (!word.isEmpty()) {
                    sb.append(word.charAt(0));
                }
            }
            base = sb.toString();
        } else {
            if (name.length() >= 3) {
                base = name.substring(0, 3);
            } else {
                base = name;
            }
        }
        return base;
    }

    private void seedProductionFutures() {
        try {
            List<Future> futures = new ArrayList<>();
            List<String[]> records = new ArrayList<>();
            InputStream is = getClass().getResourceAsStream("future_data.csv");
            if (is == null) {
                throw new IllegalArgumentException("File not found!");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] fields = line.split(",");
                records.add(fields);
            }

            int i = 1;
            long id = 5215;
            long ms_id = 0;
            for (String[] record : records) {
                String name = record[0];
                long size = Long.parseLong(record[1]);
                UnitName unitName =
                    UnitName.valueOf(
                        record[2].toUpperCase()
                            .replace(" ", "_")
                    );
                long margin = Long.parseLong(record[3]);
                String type = record[4];

                OffsetDateTime settlementDate =
                    OffsetDateTime.now()
                        .plusMonths(i);
                i = (i % 28) + 1;

                String ticker = makeFakeTickerForFuture(name, settlementDate);
                futures.add(
                    Future.builder()
                        .id(new UUID(ms_id, id++))
                        .name(name)
                        .contractSize(size)
                        .contractUnit(unitName)
                        .settlementDate(settlementDate)
                        .ticker(ticker)
                        .build()
                );
            }
            futureRepository.saveAllAndFlush(futures);
            LOGGER.info("future contracts seeded successfully");
        } catch (Exception e) {
            LOGGER.error("Error occurred while reading CSV file: {}", e.getMessage());
        }

    }

    private void seedProductionExchanges() {
        try {
            List<Exchange> exchanges = new ArrayList<>();
            InputStream is = getClass().getResourceAsStream("exchanges.csv");
            if (is == null) {
                throw new IllegalArgumentException("Exchange CSV file not found!");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean isHeader = true;
            long id = 7000;
            long ms_id = 0;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] fields = line.split(",");

                String name = fields[0];
                String acronym = fields[1];
                String micCode = fields[2];
                String country = fields[3];
                String currency = fields[4];
                String timeZone = fields[5];
                LocalTime openTime = LocalTime.parse(fields[6].strip());
                LocalTime closeTime = LocalTime.parse(fields[7].strip());

                LocalDate fixedDate = LocalDate.of(2000, 1, 1);
                LocalDateTime openDateTimeLocal = LocalDateTime.of(fixedDate, openTime);
                LocalDateTime closeDateTimeLocal = LocalDateTime.of(fixedDate, closeTime);

                ZoneId zoneId = ZoneId.of(timeZone);
                ZoneOffset openOffset =
                    zoneId.getRules()
                        .getOffset(openDateTimeLocal);
                ZoneOffset closeOffset =
                    zoneId.getRules()
                        .getOffset(closeDateTimeLocal);

                OffsetDateTime openDateTime = OffsetDateTime.of(openDateTimeLocal, openOffset);
                OffsetDateTime closeDateTime = OffsetDateTime.of(closeDateTimeLocal, closeOffset);

                CurrencyCode cc = null;
                try {
                    cc = CurrencyMapper.mapToCurrencyCode(currency);
                    if (cc == null) {
                        throw new IllegalArgumentException();
                    }
                } catch (IllegalArgumentException e) {
                    continue;
                }

                Exchange exchange =
                    Exchange.builder()
                        .id(new UUID(ms_id, id++))
                        .timeZone(timeZone)
                        .exchangeMICCode(micCode)
                        .currency(cc)
                        .polity(country)
                        .exchangeAcronym(acronym)
                        .exchangeName(name)
                        .openTime(openDateTime)
                        .closeTime(closeDateTime)
                        .createdAt(LocalDate.now())
                        .build();

                exchanges.add(exchange);
            }

            String timeZone = "Europe/Belgrade";

            LocalTime openTime = LocalTime.of(0, 0);
            LocalTime closeTime = LocalTime.of(23, 59);

            LocalDate fixedDate = LocalDate.of(2000, 1, 1);
            LocalDateTime openDateTimeLocal = LocalDateTime.of(fixedDate, openTime);
            LocalDateTime closeDateTimeLocal = LocalDateTime.of(fixedDate, closeTime);

            ZoneId zoneId = ZoneId.of(timeZone);
            ZoneOffset openOffset =
                zoneId.getRules()
                    .getOffset(openDateTimeLocal);
            ZoneOffset closeOffset =
                zoneId.getRules()
                    .getOffset(closeDateTimeLocal);

            OffsetDateTime openDateTime = OffsetDateTime.of(openDateTimeLocal, openOffset);
            OffsetDateTime closeDateTime = OffsetDateTime.of(closeDateTimeLocal, closeOffset);

            exchanges.add(
                Exchange.builder()
                    .id(new UUID(ms_id, id++))
                    .timeZone(timeZone)
                    .exchangeMICCode("FORX")
                    .currency(CurrencyCode.RSD)
                    .polity("Serbia")
                    .exchangeAcronym("SRBFORX")
                    .exchangeName("Serbia Forex")
                    .openTime(openDateTime)
                    .closeTime(closeDateTime)
                    .createdAt(LocalDate.now())
                    .build()
            );

            exchanges.add(
                Exchange.builder()
                    .id(new UUID(ms_id, id++))
                    .timeZone(timeZone)
                    .exchangeMICCode("FUTU")
                    .currency(CurrencyCode.RSD)
                    .polity("Serbia")
                    .exchangeAcronym("SRBFUTU")
                    .exchangeName("Serbia Futures")
                    .openTime(openDateTime)
                    .closeTime(closeDateTime)
                    .createdAt(LocalDate.now())
                    .build()
            );

            exchangeRepository.saveAllAndFlush(exchanges);
            LOGGER.info("Exchange data seeded successfully");
        } catch (Exception e) {
            LOGGER.error("Error occurred while reading Exchange CSV: {}", e.getMessage());
        }
    }
}
