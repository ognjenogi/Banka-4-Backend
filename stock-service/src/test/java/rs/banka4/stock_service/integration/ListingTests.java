package rs.banka4.stock_service.integration;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import rs.banka4.stock_service.repositories.AssetRepository;
import rs.banka4.stock_service.repositories.ExchangeRepository;
import rs.banka4.stock_service.repositories.ListingDailyPriceInfoRepository;
import rs.banka4.stock_service.repositories.ListingRepository;
import rs.banka4.stock_service.repositories.SecurityRepository;
import rs.banka4.stock_service.utils.AssetGenerator;
import rs.banka4.stock_service.utils.ExchangeGenerator;
import rs.banka4.stock_service.utils.ListingGenerator;
import rs.banka4.testlib.integration.DbEnabledTest;

@SpringBootTest
@DbEnabledTest
@AutoConfigureMockMvc
public class ListingTests {
    @Autowired
    private MockMvcTester mvc;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private SecurityRepository securityRepository;
    @Autowired
    private ListingRepository listingRepo;
    @Autowired
    private ExchangeRepository exchangeRepo;
    @Autowired
    private ListingDailyPriceInfoRepository listingHistoryRepo;

    @Test
    public void test_getListingDetails_basic() {
        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            ex1.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );

        mvc.get()
            /* TODO(arsen): swap with security id? */
            .uri("/listings/{stockId}", AssetGenerator.STOCK_EX1_UUID)
            .assertThat()
            .bodyJson()
            .isLenientlyEqualTo("""
                {
                  "securityType": "STOCK",
                  "name": "Example One™",
                  "ticker": "EX1",
                  "change": 21.96,
                  "price": 66.40,
                  "outstandingShares": 325000,
                  "dividendYield": 0.05
                }
                """);
    }

    @Test
    public void test_getListingOptions_basic() {
        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            ex1.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );

        final var settlementDate =
            OffsetDateTime.now()
                .plusYears(2);

        mvc.get()
            /* TODO(arsen): swap with security id? */
            .uri("/listings/options/{stockId}", AssetGenerator.STOCK_EX1_UUID)
            .param("settlementDate", settlementDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
            .assertThat()
            .bodyJson()
            /* IDs are: OPTION_EX1_{PUT,CALL}{,2}_UUID in AssetGenerator */
            .isLenientlyEqualTo("""
                [
                  {
                    "callsId": "b5b843a4-c90c-4a14-8bdf-06da1b543f97",
                    "callsLastPrice": 0,
                    "callsChange": 0,
                    "callsImpliedVolatility": 337.8,
                    "callsVolume": 0,
                    "callsOpenInterest": 445,
                    "strike": 170.00,
                    "putsId": "65d965c1-0d7c-4506-a424-4fe81a1f6357",
                    "putsLastPrice": 0,
                    "putsChange": 0,
                    "putsImpliedVolatility": 412.5,
                    "putsVolume": 0,
                    "putsOpenInterest": 565
                  },

                  {
                    "callsId": "964919c6-2e54-4de2-8115-24f44a5c2ea5",
                    "callsLastPrice": 0,
                    "callsChange": 0,
                    "callsImpliedVolatility": 122.4,
                    "callsVolume": 0,
                    "callsOpenInterest": 914,
                    "strike": 140.00,
                    "putsId": "18995f03-540e-455e-b44c-ce819c00e562",
                    "putsLastPrice": 0,
                    "putsChange": 0,
                    "putsImpliedVolatility": 338.9,
                    "putsVolume": 0,
                    "putsOpenInterest": 878
                  }
                ]
                """);
    }

    /**
     * Verify that getPriceChanges returns all changes from oldest to newest.
     */
    @Test
    public void test_getPriceChanges_basic() {
        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            ex1.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );

        mvc.get()
            .uri("/listings/priceChange")
            .assertThat()
            .bodyJson()
            .isLenientlyEqualTo("""
                [
                  {
                    "price": 28.74
                  },
                  {
                    "price": 5.90
                  },
                  {
                    "price": 12.18
                  },
                  {
                    "price": 19.21
                  },
                  {
                    "price": 44.44
                  }
                ]
                """)
            .extractingPath("$..date")
            .asArray()
            .isSortedAccordingTo(
                Comparator.comparing(
                    maybeDate -> OffsetDateTime.parse(
                        (String) maybeDate,
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME
                    )
                )
            );
    }

    /**
     * Verify that getListings returns all up-to-date listings. Given that we only generate a bunch
     * of listings for {@link AssetGenerator#STOCK_EX1_UUID} currently, that will be only one
     * element.
     */
    @Test
    public void test_getListings_no_filter_only_one_stock() {
        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            ex1.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );

        mvc.get()
            .uri("/listings?page=0&size=2")
            .assertThat()
            .bodyJson()
            .isLenientlyEqualTo("""
                {
                  "content": [
                    {
                      "name": "Example One™",
                      "ticker": "EX1",
                      "volume": 0,
                      "change": 21.96,
                      "price": 66.40
                    }
                  ],
                  "totalElements": 1
                }
                """)
            .extractingPath("$.content")
            .asArray()
            .hasSize(1);
    }
}
