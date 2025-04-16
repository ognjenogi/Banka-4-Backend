package rs.banka4.bank_service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import rs.banka4.bank_service.domain.account.db.Account;
import rs.banka4.bank_service.domain.taxes.db.UserTaxDebts;
import rs.banka4.bank_service.domain.user.client.db.Client;
import rs.banka4.bank_service.generator.AccountObjectMother;
import rs.banka4.bank_service.integration.generator.UserGenerator;
import rs.banka4.bank_service.integration.generator.UserTaxGenerator;
import rs.banka4.bank_service.repositories.*;
import rs.banka4.bank_service.runners.TestDataRunner;
import rs.banka4.bank_service.service.abstraction.ExchangeRateService;
import rs.banka4.bank_service.service.abstraction.TaxService;
import rs.banka4.bank_service.service.impl.TaxServiceImp;
import rs.banka4.rafeisen.common.currency.CurrencyCode;
import rs.banka4.testlib.integration.DbEnabledTest;
import rs.banka4.testlib.utils.JwtPlaceholders;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DbEnabledTest
@AutoConfigureMockMvc
public class TaxTests {
    @Autowired
    private MockMvcTester mvc;
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
    private TaxServiceImp taxService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    ExchangeRateService exchangeRateService;
    @Autowired
    private UserTaxDebtsRepository userTaxDebtsRepository;

    private Client createTestClient() {
        final var assetOwner =
            userGen.createClient(
                x -> x.id(JwtPlaceholders.CLIENT_ID)
                    .email("johndqoe@example.com")
            );
        return userRepository.save(assetOwner);
    }

    private Client createTestClient2() {
        final var assetOwner =
            userGen.createClient(
                x -> x.id(UUID.randomUUID()).firstName("Michael").lastName("Smith")
                    .email("johndqoeads@example.com")
            );
        return userRepository.save(assetOwner);
    }
    private Account createStateAccount(Client client) {
        var account = AccountObjectMother.generateBasicToAccount();
        account.setClient(client);
        account.setAccountNumber(TestDataRunner.STATE_ACCOUNT_NUMBER);
        userRepository.save(account.getEmployee());
        return accountRepository.save(account);
    }
    private Client createTestClient3() {
        final var assetOwner =
            userGen.createClient(
                x -> x.id(UUID.randomUUID())
                    .email("johndqoeads@addasd.com")
            );
        return userRepository.save(assetOwner);
    }

    @Test
    public void testTaxSummary() {
        Client client = createTestClient();

        UserTaxGenerator.createDummyTax(client, userRepository, accountRepository, userTaxDebtsRepository);
        UserTaxGenerator.createDummyTaxEur(client, userRepository, accountRepository, userTaxDebtsRepository);

        String expectedJson = """
            {
               "content":[
                  {
                     "userId":"a4bf370e-2129-4116-9243-0c4ead0fe43e",
                     "firstName":"John",
                     "email":"johndqoe@example.com",
                     "lastName":"Doe",
                     "unpaidTax":6020.2813599062140000,
                     "currency":"RSD"
                  }
               ],
               "page":{
                  "size":10,
                  "number":0,
                  "totalElements":1,
                  "totalPages":1
               }
            }
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/tax/summary")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }

    @Test
    public void testTaxSummaryMultipleAccountsMultipleUsers() {
        Client client1 = createTestClient();
        Client client2 = createTestClient2();
        UserTaxGenerator.createMultipleDebts(client1, 2, userRepository, accountRepository, userTaxDebtsRepository);
        UserTaxGenerator.createMultipleDebts(client2, 2, userRepository, accountRepository, userTaxDebtsRepository);

        String expectedJson = """
            {
               "content":[
                  {
                     "firstName":"John",
                     "email":"johndqoe@example.com",
                     "lastName":"Doe",
                     "unpaidTax":11940.5627198124280000,
                     "currency":"RSD"
                  },
                  {
                     "firstName":"Michael",
                     "email":"johndqoeads@example.com",
                     "lastName":"Smith",
                     "unpaidTax":11940.5627198124280000,
                     "currency":"RSD"
                  }
               ],
               "page":{
                  "size":10,
                  "number":0,
                  "totalElements":2,
                  "totalPages":1
               }
            }`
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/tax/summary")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }
    @Test
    public void testTaxSummaryMultipleAccountsMultipleUsersSearchByName() {
        Client client1 = createTestClient();
        Client client2 = createTestClient2();
        UserTaxGenerator.createMultipleDebts(client1, 2, userRepository, accountRepository, userTaxDebtsRepository);
        UserTaxGenerator.createMultipleDebts(client2, 2, userRepository, accountRepository, userTaxDebtsRepository);

        String expectedJson = """
            {
               "content":[
                  {
                     "firstName":"John",
                     "email":"johndqoe@example.com",
                     "lastName":"Doe",
                     "unpaidTax":11940.5627198124280000,
                     "currency":"RSD"
                  }
               ],
               "page":{
                  "size":10,
                  "number":0,
                  "totalElements":1,
                  "totalPages":1
               }
            }`
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/tax/summary")
            .param("firstName","Joh")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }
    @Test
    public void testTaxSummaryMultipleAccountsMultipleUsersSearchByLastName() {
        Client client1 = createTestClient();
        Client client2 = createTestClient2();
        UserTaxGenerator.createMultipleDebts(client1, 2, userRepository, accountRepository, userTaxDebtsRepository);
        UserTaxGenerator.createMultipleDebts(client2, 2, userRepository, accountRepository, userTaxDebtsRepository);

        String expectedJson = """
            {
               "content":[
                  {
                     "firstName":"Michael",
                     "email":"johndqoeads@example.com",
                     "lastName":"Smith",
                     "unpaidTax":11940.5627198124280000,
                     "currency":"RSD"
                  }
               ],
               "page":{
                  "size":10,
                  "number":0,
                  "totalElements":1,
                  "totalPages":1
               }
            }`
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/tax/summary")
            .param("lastName","Smit")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }
    @Test
    public void testTaxSummaryMultipleAccountsMultipleUsersSearchByFullName() {
        Client client1 = createTestClient();
        Client client2 = createTestClient2();
        UserTaxGenerator.createMultipleDebts(client1, 2, userRepository, accountRepository, userTaxDebtsRepository);
        UserTaxGenerator.createMultipleDebts(client2, 2, userRepository, accountRepository, userTaxDebtsRepository);

        String expectedJson = """
            {
               "content":[
                  {
                     "firstName":"Michael",
                     "email":"johndqoeads@example.com",
                     "lastName":"Smith",
                     "unpaidTax":11940.5627198124280000,
                     "currency":"RSD"
                  }
               ],
               "page":{
                  "size":10,
                  "number":0,
                  "totalElements":1,
                  "totalPages":1
               }
            }`
            """;

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/stock/tax/summary")
            .param("lastName","Smi")
            .param("firstName","Micha")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);
    }
    @Test
    public void testTaxSpecificClient() {
        Client client1 = createTestClient();
        Client client2 = createTestClient2();
        Client client3 = createTestClient3();
        var stateAcc = createStateAccount(client3);
        var stateBalance = stateAcc.getAvailableBalance();
        var debt = UserTaxGenerator.createMultipleDebtsRSD(client1, 2, userRepository, accountRepository, userTaxDebtsRepository);
        UserTaxGenerator.createMultipleDebts(client2, 2, userRepository, accountRepository, userTaxDebtsRepository);

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.post()
            .uri("/stock/tax/collect/"+client1.getId())
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk();
        var userDebts = userTaxDebtsRepository.findByAccount_Client_Id(client1.getId());
        userDebts.forEach(userDebt -> {
            assertEquals(BigDecimal.ZERO, userDebt.getDebtAmount().stripTrailingZeros());
        });
        var stateBalanceAfter = accountRepository.findAccountByAccountNumber(TestDataRunner.STATE_ACCOUNT_NUMBER).orElseThrow().getAvailableBalance();
        assertEquals(stateBalance.add(debt).stripTrailingZeros(), stateBalanceAfter.stripTrailingZeros());
    }
    @Test
    public void testTaxSpecificClientNotRSD() {
        Client client1 = createTestClient();
        Client client3 = createTestClient3();
        var stateAcc = createStateAccount(client3);
        var stateBalance = stateAcc.getAvailableBalance();
        var debt = UserTaxGenerator.createDummyTaxEur(client1, userRepository, accountRepository, userTaxDebtsRepository);

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.post()
            .uri("/stock/tax/collect/"+client1.getId())
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk();
        var userDebts = userTaxDebtsRepository.findByAccount_Client_Id(client1.getId());
        userDebts.forEach(userDebt -> {
            assertEquals(BigDecimal.ZERO, userDebt.getDebtAmount().stripTrailingZeros());
        });
        var stateBalanceAfter = accountRepository.findAccountByAccountNumber(TestDataRunner.STATE_ACCOUNT_NUMBER).orElseThrow().getAvailableBalance();
        debt = exchangeRateService.convertCurrency(debt, CurrencyCode.EUR,CurrencyCode.RSD);
        assertEquals(stateBalance.add(debt).stripTrailingZeros().round(MathContext.DECIMAL32), stateBalanceAfter.stripTrailingZeros().round(MathContext.DECIMAL32));
    }
    @Test
    public void testTaxSpecificClientInsufficientFunds() {
        Client client1 = createTestClient();
        Client client2 = createTestClient2();
        Client client3 = createTestClient3();
        var stateAcc = createStateAccount(client3);
        UserTaxGenerator.createMultipleDebtsInsufficientFunds(client1, 2, userRepository, accountRepository, userTaxDebtsRepository);
        UserTaxGenerator.createMultipleDebts(client2, 2, userRepository, accountRepository, userTaxDebtsRepository);

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.post()
            .uri("/stock/tax/collect/"+client1.getId())
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat().hasStatus(HttpStatus.FORBIDDEN);
    }
    @Test
    public void testTaxAllClients() {
        Client client1 = createTestClient();
        Client client2 = createTestClient2();
        Client client3 = createTestClient3();
        var stateAcc = createStateAccount(client3);
        var stateBalance = stateAcc.getAvailableBalance();
        var debt = UserTaxGenerator.createMultipleDebtsRSD(client1, 2, userRepository, accountRepository, userTaxDebtsRepository);
        var debt2 =UserTaxGenerator.createMultipleDebtsEUR(client2, 2, userRepository, accountRepository, userTaxDebtsRepository);

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.post()
            .uri("/stock/tax/trigger-monthly")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk();
        var allDebts = userTaxDebtsRepository.findAll();
        allDebts.forEach(userDebt -> {
            assertEquals(BigDecimal.ZERO, userDebt.getDebtAmount().stripTrailingZeros());
        });
        debt2 = exchangeRateService.convertCurrency(debt2, CurrencyCode.EUR,CurrencyCode.RSD);
        var stateBalanceAfter = accountRepository.findAccountByAccountNumber(TestDataRunner.STATE_ACCOUNT_NUMBER).orElseThrow().getAvailableBalance();
        assertEquals(stateBalance.add(debt).add(debt2).stripTrailingZeros().round(MathContext.DECIMAL32), stateBalanceAfter.stripTrailingZeros().round(MathContext.DECIMAL32));
    }
    @Test
    public void testTaxAllClientsOneInsufficientFunds() {
        Client client1 = createTestClient();
        Client client2 = createTestClient2();
        Client client3 = createTestClient3();
        var stateAcc = createStateAccount(client3);
        var stateBalance = stateAcc.getAvailableBalance();
        var debt = UserTaxGenerator.createMultipleDebtsRSD(client1, 2, userRepository, accountRepository, userTaxDebtsRepository);
        UserTaxGenerator.createMultipleDebtsInsufficientFunds(client2, 2, userRepository, accountRepository, userTaxDebtsRepository);

        String jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.post()
            .uri("/stock/tax/trigger-monthly")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk();
        var stateBalanceAfter = accountRepository.findAccountByAccountNumber(TestDataRunner.STATE_ACCOUNT_NUMBER).orElseThrow().getAvailableBalance();
        assertEquals(stateBalance.add(debt).stripTrailingZeros().round(MathContext.DECIMAL32), stateBalanceAfter.stripTrailingZeros().round(MathContext.DECIMAL32));
    }
    @Test
    public void testTaxYearlyClean() {
        Client client1 = createTestClient();
        Client client2 = createTestClient2();
        Client client3 = createTestClient3();
        createStateAccount(client3);
        UserTaxGenerator.createMultipleDebtsRSD(client1, 2, userRepository, accountRepository, userTaxDebtsRepository);
        UserTaxGenerator.createMultipleDebtsEUR(client2, 2, userRepository, accountRepository, userTaxDebtsRepository);

        taxService.cleanYearlyDebt();

        var allDebts = userTaxDebtsRepository.findAll();
        allDebts.forEach(userDebt -> {
            assertEquals(BigDecimal.ZERO, userDebt.getYearlyDebtAmount().stripTrailingZeros());
        });
    }
}
