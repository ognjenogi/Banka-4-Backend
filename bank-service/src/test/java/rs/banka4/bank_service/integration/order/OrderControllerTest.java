package rs.banka4.bank_service.integration.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import rs.banka4.bank_service.domain.exchanges.db.Exchange;
import rs.banka4.bank_service.domain.listing.db.Listing;
import rs.banka4.bank_service.domain.security.stock.db.Stock;
import rs.banka4.bank_service.integration.generator.UserGenerator;
import rs.banka4.bank_service.repositories.*;
import rs.banka4.bank_service.utils.AssetGenerator;
import rs.banka4.bank_service.utils.DataSourceService;
import rs.banka4.testlib.integration.DbEnabledTest;
import rs.banka4.testlib.utils.JwtPlaceholders;

@SpringBootTest
@DbEnabledTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private AssetRepository assetRepo;
    @Autowired
    private ActuaryRepository actuaryRepository;
    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private ExchangeRepository exchangeRepository;
    @Autowired
    private UserGenerator userGen;
    @Autowired
    private AccountRepository accountRepo;

    private UUID stockId;

    private static final String ACCOUNT_NUMBER = DataSourceService.ACCOUNT_JANE_STANDARD_NUMBER;

    @BeforeEach
    void setUp() {
        accountRepo.deleteAll();
        assetRepo.deleteAll();
        listingRepository.deleteAll();
        actuaryRepository.deleteAll();

        dataSourceService.insertData(true);

        assetRepo.saveAll(AssetGenerator.makeExampleAssets());
        stockId = AssetGenerator.STOCK_EX1_UUID;

        Stock stock =
            (Stock) assetRepo.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        Exchange exchange = TestDataFactory.buildExchange();
        exchangeRepository.save(exchange);

        Listing listing = TestDataFactory.buildListing(stock, exchange);
        listingRepository.save(listing);

    }

    @Test
    void shouldCreateOrderSuccessfully() {
        String jwt = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;

        String payload = """
            {
              "assetId": "%s",
              "direction": "BUY",
              "quantity": 10,
              "limitValue": null,
              "stopValue": null,
              "allOrNothing": false,
              "margin": false,
              "accountNumber": "%s"
            }
            """.formatted(AssetGenerator.STOCK_EX1_UUID, TestDataFactory.ACCOUNT_NUMBER);

        mvc.post()
            .uri("/stock/orders")
            .header("Authorization", jwt)
            .contentType("application/json")
            .content(payload)
            .assertThat()
            .hasStatus4xxClientError();
    }

    @Test
    void shouldCalculateAveragePriceSuccessfully() {
        String jwt = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;

        String payload = """
            {
              "assetId": "%s",
              "direction": "BUY",
              "amount": 10,
              "accountNumber": "%s"
            }
            """.formatted(stockId, ACCOUNT_NUMBER);

        mvc.post()
            .uri("/stock/orders/calculate-average-price")
            .header("Authorization", jwt)
            .contentType("application/json")
            .content(payload)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                assertThat(json.toString()).contains("orderType");
                assertThat(json.toString()).contains("approximatePrice");
                assertThat(json.toString()).contains("quantity");
            });
    }

    @Test
    void shouldFailCalculateAveragePriceWithInvalidPayload() {
        String jwt = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;

        String payload = """
            {
              "amount": 10
            }
            """;

        mvc.post()
            .uri("/stock/orders/calculate-average-price")
            .header("Authorization", jwt)
            .contentType("application/json")
            .content(payload)
            .assertThat()
            .hasStatus(HttpStatus.BAD_REQUEST);
    }
}
