package rs.banka4.bank_service.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.assets.db.AssetOwnership;
import rs.banka4.bank_service.domain.assets.db.AssetOwnershipId;
import rs.banka4.bank_service.domain.options.db.Asset;
import rs.banka4.bank_service.domain.orders.db.Direction;
import rs.banka4.bank_service.domain.orders.db.Order;
import rs.banka4.bank_service.domain.orders.db.OrderType;
import rs.banka4.bank_service.domain.orders.db.Status;
import rs.banka4.bank_service.domain.security.stock.db.Stock;
import rs.banka4.bank_service.domain.taxes.db.UserTaxDebts;
import rs.banka4.bank_service.domain.user.User;
import rs.banka4.bank_service.domain.user.client.db.Client;
import rs.banka4.bank_service.generator.AccountObjectMother;
import rs.banka4.bank_service.integration.generator.UserGenerator;
import rs.banka4.bank_service.repositories.*;
import rs.banka4.bank_service.utils.AssetGenerator;
import rs.banka4.bank_service.utils.ExchangeGenerator;
import rs.banka4.bank_service.utils.ListingGenerator;
import rs.banka4.rafeisen.common.currency.CurrencyCode;
import rs.banka4.testlib.integration.DbEnabledTest;
import rs.banka4.testlib.utils.JwtPlaceholders;

@SpringBootTest
@DbEnabledTest
@AutoConfigureMockMvc
public class SecuritiesControllerTests {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private AssetOwnershipRepository assetOwnershipRepository;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserGenerator userGen;
    @Autowired
    private AccountRepository accountRepository;
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
    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTaxDebtsRepository userTaxDebtsRepository;

    private void createDummyTax(Client client) {
        var account = AccountObjectMother.generateBasicToAccount();
        account.setClient(client);
        userRepository.save(account.getEmployee());
        accountRepository.save(account);
        var dept =
            UserTaxDebts.builder()
                .debtAmount(BigDecimal.valueOf(100))
                .yearlyDebtAmount(BigDecimal.valueOf(1000))
                .account(account)
                .build();
        userTaxDebtsRepository.save(dept);
    }

    private void createDummyTaxEur(Client client) {
        var account = AccountObjectMother.generateBasicEURFromAccount();
        account.setClient(client);
        userRepository.save(account.getEmployee());
        accountRepository.save(account);
        var dept =
            UserTaxDebts.builder()
                .debtAmount(BigDecimal.valueOf(50))
                .yearlyDebtAmount(BigDecimal.valueOf(200))
                .account(account)
                .build();
        userTaxDebtsRepository.save(dept);
    }

    private void createDummyAssetOwnership(
        User userId,
        Asset asset,
        int privateAmt,
        int publicAmt,
        int reservedAmt
    ) {
        AssetOwnershipId ownershipId = new AssetOwnershipId(userId, asset);
        AssetOwnership ownership = new AssetOwnership();
        ownership.setId(ownershipId);
        ownership.setPrivateAmount(privateAmt);
        ownership.setPublicAmount(publicAmt);
        ownership.setReservedAmount(reservedAmt);
        assetOwnershipRepository.save(ownership);
    }

    private void createDummyAssetOwnership2(
        User userId,
        Asset asset,
        int privateAmt,
        int publicAmt,
        int reservedAmt
    ) {
        AssetOwnershipId ownershipId = new AssetOwnershipId(userId, asset);
        AssetOwnership ownership = new AssetOwnership();
        ownership.setId(ownershipId);
        ownership.setPrivateAmount(privateAmt);
        ownership.setPublicAmount(publicAmt);
        ownership.setReservedAmount(reservedAmt);
        assetOwnershipRepository.save(ownership);
    }

    private void createDummyBuyOrder(
        Client userId,
        Asset asset,
        int quantity,
        BigDecimal priceValue,
        CurrencyCode currency
    ) {
        var account = AccountObjectMother.generateBasicToAccount();
        account.setClient(userId);
        userRepository.save(account.getEmployee());
        accountRepository.save(account);
        Order buyOrder =
            Order.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .user(userId)
                .asset(asset)
                .orderType(OrderType.MARKET)
                .quantity(quantity)
                .contractSize(1)
                .pricePerUnit(new MonetaryAmount(priceValue, currency))
                .direction(Direction.BUY)
                .status(Status.APPROVED)
                .approvedBy(null)
                .isDone(true)
                .lastModified(OffsetDateTime.now())
                .createdAt(OffsetDateTime.now())
                .remainingPortions(100)
                .afterHours(false)
                .limitValue(null)
                .stopValue(null)
                .allOrNothing(false)
                .margin(false)
                .account(account)
                .used(false)
                .build();
        orderRepository.save(buyOrder);
    }

    private void createDummyBuyOrder2(
        Client userId,
        Asset asset,
        int quantity,
        BigDecimal priceValue,
        CurrencyCode currency
    ) {
        var account = AccountObjectMother.generateBasicFromAccount();
        account.setClient(userId);
        userRepository.save(account.getEmployee());
        accountRepository.save(account);
        Order buyOrder =
            Order.builder()
                .user(userId)
                .asset(asset)
                .orderType(OrderType.MARKET)
                .quantity(quantity)
                .contractSize(1)
                .pricePerUnit(new MonetaryAmount(priceValue, currency))
                .direction(Direction.BUY)
                .status(Status.APPROVED)
                .approvedBy(null)
                .isDone(true)
                .lastModified(OffsetDateTime.now())
                .createdAt(OffsetDateTime.now())
                .remainingPortions(100)
                .afterHours(false)
                .limitValue(null)
                .stopValue(null)
                .allOrNothing(false)
                .margin(false)
                .account(account)
                .used(false)
                .build();
        orderRepository.save(buyOrder);
    }

    private void createDummyBuyOrderSTOP(
        Client userId,
        Asset asset,
        int quantity,
        BigDecimal priceValue,
        CurrencyCode currency
    ) {
        var account = AccountObjectMother.generateBasicToAccount();
        account.setClient(userId);
        userRepository.save(account.getEmployee());
        accountRepository.save(account);
        Order buyOrder =
            Order.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .user(userId)
                .asset(asset)
                .orderType(OrderType.STOP)
                .quantity(quantity)
                .contractSize(1)
                .pricePerUnit(new MonetaryAmount(priceValue, currency))
                .direction(Direction.BUY)
                .status(Status.APPROVED)
                .approvedBy(null)
                .isDone(true)
                .lastModified(OffsetDateTime.now())
                .createdAt(OffsetDateTime.now())
                .remainingPortions(100)
                .afterHours(false)
                .limitValue(null)
                .stopValue(null)
                .allOrNothing(false)
                .margin(false)
                .account(account)
                .used(false)
                .build();
        orderRepository.save(buyOrder);
    }

    private void createDummySellOrder(
        Client userId,
        Asset asset,
        int quantity,
        BigDecimal priceValue,
        CurrencyCode currency
    ) {
        var account = AccountObjectMother.generateBasicEURFromAccount();
        account.setClient(userId);
        account.setAccountNumber(
            UUID.randomUUID()
                .toString()
        );
        userRepository.save(account.getEmployee());
        accountRepository.save(account);
        Order buyOrder =
            Order.builder()
                .user(userId)
                .asset(asset)
                .orderType(OrderType.MARKET)
                .quantity(quantity)
                .contractSize(1)
                .pricePerUnit(new MonetaryAmount(priceValue, currency))
                .direction(Direction.SELL)
                .status(Status.APPROVED)
                .approvedBy(null)
                .isDone(true)
                .lastModified(OffsetDateTime.now())
                .createdAt(OffsetDateTime.now())
                .remainingPortions(100)
                .afterHours(false)
                .limitValue(null)
                .stopValue(null)
                .allOrNothing(false)
                .margin(false)
                .account(account)
                .used(false)
                .build();
        orderRepository.save(buyOrder);
    }

    private Client createTestClient() {
        final var assetOwner =
            userGen.createClient(
                x -> x.id(JwtPlaceholders.CLIENT_ID)
                    .email("johndqoe@example.com")
            );
        return userRepository.save(assetOwner);
    }

    @Test
    public void testGetMyPortfolioWithStock() {
        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var stock = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            stock.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );
        final var assetOwner = createTestClient();
        createDummyBuyOrder(
            assetOwner,
            (Stock) stock.get(),
            100,
            BigDecimal.valueOf(1),
            CurrencyCode.USD
        );
        createDummyAssetOwnership(assetOwner, (Stock) stock.get(), 100, 0, 0);

        String expectedJson = """
            {
              "content": [
                {
                   "assetType": STOCK,
                  "ticker": "EX1",
                  "amount": 100,
                  "price": {"amount": 75.14, "currency": "USD"},
                  "profit": {"amount": 7407.0, "currency": "USD"},
                  "publicAmount": 0
                }
              ],
              "page": {
                "size": 10,
                "number": 0,
                "totalElements": 1,
                "totalPages": 1
              }
            }
            """;
        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/me")
            .param("page", "0")
            .param("size", "10")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }

    @Test
    public void testEmptyPortfolio() {
        createTestClient();

        String expectedJson = """
            {
              "content": [],
              "page": {
                "size": 10,
                "number": 0,
                "totalElements": 0,
                "totalPages": 0
              }
            }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/me")
            .param("page", "0")
            .param("size", "10")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }

    @Test
    public void testStockZeroProfit() {
        Client client = createTestClient();
        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var stock = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            stock.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );
        createDummyAssetOwnership(client, stock.get(), 100, 0, 0);
        createDummyBuyOrderSTOP(
            client,
            stock.get(),
            100,
            BigDecimal.valueOf(75.14),
            CurrencyCode.USD
        );

        String expectedJson = """
            {
              "content": [
                {
                  "ticker": "EX1",
                  "amount": 100,
                  "price": {"amount": 75.14, "currency": "USD"},
                  "profit": {"amount": 0, "currency": "USD"},
                  "publicAmount": 0
                }
              ],
              "page": {
                "size": 10,
                "number": 0,
                "totalElements": 1,
                "totalPages": 1
              }
            }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/me")
            .param("page", "0")
            .param("size", "10")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }

    @Test
    public void testStockInProfit() {
        Client client = createTestClient();

        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);
        var stock = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            stock.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );

        createDummyAssetOwnership(client, stock.get(), 99, 1, 0);
        createDummyBuyOrder(client, stock.get(), 100, BigDecimal.valueOf(40), CurrencyCode.USD);


        String expectedJson = """
            {
              "content": [
                {
                  "ticker": "EX1",
                  "amount": 100,
                  "price": {"amount": 75.14, "currency": "USD"},
                  "profit": {"amount": 3507.0, "currency": "USD"},
                  "publicAmount": 1
                }
              ],
              "page": {
                "size": 10,
                "number": 0,
                "totalElements": 1,
                "totalPages": 1
              }
            }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/me")
            .param("page", "0")
            .param("size", "10")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }

    @Test
    public void testStockNegativeProfit() {
        Client client = createTestClient();

        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);
        var stock = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            stock.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );

        createDummyAssetOwnership(client, stock.get(), 10, 0, 0);
        createDummyBuyOrder(client, stock.get(), 10, BigDecimal.valueOf(100), CurrencyCode.USD);

        String expectedJson = """
            {
              "content": [
                {
                  "ticker": "EX1",
                  "amount": 10,
                  "price": {"amount": 75.14, "currency": "USD"},
                  "profit": {"amount": -255.6, "currency": "USD"},
                  "publicAmount": 0
                }
              ],
              "page": {
                "size": 10,
                "number": 0,
                "totalElements": 1,
                "totalPages": 1
              }
            }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/me")
            .param("page", "0")
            .param("size", "10")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }

    @Test
    public void testOptionInMoney() {
        Client client = createTestClient();
        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);
        var option = assetRepository.findById(AssetGenerator.OPTION_EX1_CALL_UUID);
        var stock = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            stock.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );
        createDummyAssetOwnership(client, option.get(), 1, 0, 0);

        String expectedJson = """
            {
              "content": [
                {
                  "assetType": OPTION,
                  "amount": 1,
                  "price": {"amount":75.14 , "currency": "USD"},
                  "profit": {"amount": -150.0, "currency": "USD"},
                  "publicAmount": 0
                }
              ],
              "page": {
                "size": 10,
                "number": 0,
                "totalElements": 1,
                "totalPages": 1
              }
            }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/me")
            .param("page", "0")
            .param("size", "10")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }

    @Test
    public void testOptionOutOfMoney() {
        Client client = createTestClient();
        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);
        var option = assetRepository.findById(AssetGenerator.OPTION_EX1_PUT_UUID);
        var stock = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            stock.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );
        createDummyAssetOwnership(client, option.get(), 1, 0, 0);

        String expectedJson = """
            {
              "content": [
                {
                  "amount": 1,
                  "price": {"amount": 75.14, "currency": "USD"},
                  "profit": {"amount": 9336.0, "currency": "USD"},
                  "publicAmount": 0
                }
              ],
              "page": {
                "size": 10,
                "number": 0,
                "totalElements": 1,
                "totalPages": 1
              }
            }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/me")
            .param("page", "0")
            .param("size", "10")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }


    @Test
    public void testFuturesProfit() {
        Client client = createTestClient();

        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var future = securityRepository.findById(AssetGenerator.FUTURE_CRUDE_OIL_UUID);
        ListingGenerator.makeExampleListings(
            future.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );

        createDummyAssetOwnership(client, future.get(), 2, 0, 0);
        createDummyBuyOrder(client, future.get(), 2, BigDecimal.valueOf(50), CurrencyCode.USD);

        String expectedJson = """
            {
              "content": [
                {
                  "assetType": FUTURE,
                  "ticker": "FUT",
                  "amount": 2,
                  "price": {"amount": 80.48, "currency": "USD"},
                  "profit": {"amount": 53960.0, "currency": "USD"},
                  "publicAmount": 0
                }
              ],
              "page": {
                "size": 10,
                "number": 0,
                "totalElements": 1,
                "totalPages": 1
              }
            }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/me")
            .param("page", "0")
            .param("size", "10")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }

    @Test
    public void testForexProfit() {
        Client client = createTestClient();

        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);
        var future = securityRepository.findById(AssetGenerator.FOREX_EUR_USD_UUID);
        ListingGenerator.makeExampleListings(
            future.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );

        createDummyAssetOwnership(client, future.get(), 1000, 0, 0);
        createDummyBuyOrder(client, future.get(), 1000, BigDecimal.valueOf(1.05), CurrencyCode.USD);

        String expectedJson = """
            {
              "content": [
                {
                "assetType": FOREX_PAIR,
                  "ticker": "USD/EUR",
                  "amount": 1000,
                  "price": {"amount": 83.95, "currency": "USD"},
                  "profit": {"amount": 82893.0, "currency": "USD"},
                  "publicAmount": 0
                }
              ],
              "page": {
                "size": 10,
                "number": 0,
                "totalElements": 1,
                "totalPages": 1
              }
            }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/me")
            .param("page", "0")
            .param("size", "10")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }

    @Test
    public void testMultipleHoldings() {
        Client client = createTestClient();

        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);
        var stockA = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            stockA.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );
        createDummyAssetOwnership(client, stockA.orElseThrow(), 100, 0, 0);
        createDummyBuyOrderSTOP(
            client,
            stockA.get(),
            100,
            BigDecimal.valueOf(40),
            CurrencyCode.USD
        );

        var stockB = securityRepository.findById(AssetGenerator.STOCK_EX2_UUID);
        ListingGenerator.makeExampleListings(
            stockB.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );
        createDummyAssetOwnership2(client, stockB.get(), 10, 0, 0);
        createDummyBuyOrder2(
            client,
            stockB.orElseThrow(),
            10,
            BigDecimal.valueOf(10),
            CurrencyCode.USD
        );

        String expectedJson = """
            {
              "content": [
                {
                  "ticker": "EX1",
                  "amount": 100,
                  "price": {"amount": 75.14, "currency": "USD"},
                  "profit": {"amount": 3514.0, "currency": "USD"},
                  "publicAmount": 0
                },
                {
                  "ticker": "EX2",
                  "amount": 10,
                  "price": {"amount": 46.69, "currency": "USD"},
                  "profit": {"amount": 359.9, "currency": "USD"},
                  "publicAmount": 0
                }
              ],
              "page": {
                "size": 10,
                "number": 0,
                "totalElements": 2,
                "totalPages": 1
              }
            }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/me")
            .param("page", "0")
            .param("size", "10")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }

    @Test
    public void testPartialSells() {
        Client client = createTestClient();
        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);
        var stock = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            stock.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );
        createDummyAssetOwnership(client, stock.get(), 150, 0, 0);
        createDummyBuyOrder(client, stock.get(), 200, BigDecimal.valueOf(20), CurrencyCode.USD);
        createDummySellOrder(client, stock.get(), 50, BigDecimal.valueOf(25), CurrencyCode.USD);

        String expectedJson = """
            {
              "content": [
                {
                  "ticker": "EX1",
                  "amount": 150,
                  "price": {"amount": 75.14, "currency": "USD"},
                  "profit": {"amount": 8265.75, "currency": "USD"},
                  "publicAmount": 0
                }
              ],
              "page": {
                "size": 10,
                "number": 0,
                "totalElements": 1,
                "totalPages": 1
              }
            }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/me")
            .param("page", "0")
            .param("size", "10")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }

    @Test
    public void testTotalProfitCalculation() {
        Client client = createTestClient();
        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);
        var stock = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            stock.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );
        var stock2 = securityRepository.findById(AssetGenerator.STOCK_EX2_UUID);
        ListingGenerator.makeExampleListings(
            stock2.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );
        createDummyAssetOwnership(client, stock.get(), 150, 0, 0);
        createDummyAssetOwnership2(client, stock2.get(), 150, 0, 0);
        createDummyBuyOrder(client, stock.get(), 200, BigDecimal.valueOf(20), CurrencyCode.USD);
        createDummyBuyOrder2(client, stock2.get(), 50, BigDecimal.valueOf(20), CurrencyCode.USD);
        createDummySellOrder(client, stock.get(), 50, BigDecimal.valueOf(25), CurrencyCode.USD);

        String expectedJson = """
                {
                  "amount":9593.25,
                  "currency":"USD"
                }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/profit")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }

    @Test
    public void testMyTaxCalculation() {
        Client client = createTestClient();

        createDummyTax(client);
        createDummyTaxEur(client);

        String expectedJson = """
                {
                "paidTaxThisYear":1200.0,
                "unpaidTaxThisMonth":6020.281359906214,
                "currency":"RSD"
                }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/tax")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }

    @Test
    public void testMyTaxCalculationNoTax() {
        Client client = createTestClient();

        String expectedJson = """
                {
                "paidTaxThisYear":0,
                "unpaidTaxThisMonth":0,
                "currency":"RSD"
                }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/securities/tax")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }


}
