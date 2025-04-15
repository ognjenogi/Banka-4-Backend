package rs.banka4.bank_service.integration.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rs.banka4.bank_service.domain.account.db.Account;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.exchanges.db.Exchange;
import rs.banka4.bank_service.domain.options.db.Asset;
import rs.banka4.bank_service.domain.orders.db.Direction;
import rs.banka4.bank_service.domain.orders.db.OrderType;
import rs.banka4.bank_service.domain.orders.db.Status;
import rs.banka4.bank_service.domain.orders.dtos.CreateOrderDto;
import rs.banka4.bank_service.domain.orders.dtos.CreateOrderPreviewDto;
import rs.banka4.bank_service.domain.orders.dtos.OrderDto;
import rs.banka4.bank_service.domain.orders.dtos.OrderPreviewDto;
import rs.banka4.bank_service.generator.AccountObjectMother;
import rs.banka4.bank_service.integration.generator.UserGenerator;
import rs.banka4.bank_service.repositories.*;
import rs.banka4.bank_service.service.abstraction.OrderService;
import rs.banka4.rafeisen.common.currency.CurrencyCode;
import rs.banka4.rafeisen.common.security.AuthenticatedBankUserAuthentication;
import rs.banka4.rafeisen.common.security.AuthenticatedBankUserPrincipal;
import rs.banka4.rafeisen.common.security.Privilege;
import rs.banka4.rafeisen.common.security.UserType;
import rs.banka4.testlib.integration.DbEnabledTest;

@SpringBootTest
@DbEnabledTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private ExchangeRepository exchangeRepository;
    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private ActuaryRepository actuaryRepository;
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private UserGenerator userGen;

    private UUID assetId = TestDataFactory.ASSET_ID;
    private UUID accountId = TestDataFactory.ACCOUNT_ID;
    private String accountNumber = TestDataFactory.ACCOUNT_NUMBER;
    private UUID employeeId = UUID.fromString("e9eb41cc-1989-11f0-9256-d85ed35e4427");
    private final UUID userUuid = UUID.fromString("6f72db23-afc8-4d71-b392-eb9e626ed9af");

    @BeforeEach
    void setUp() {
        final var user =
            userGen.createClient(
                x -> x.id(userUuid)
                    .email("release.me@gmail.com")
            );
        final var employee =
            userGen.createEmployee(
                x -> x.id(employeeId)
                    .email("release.me@hotmail.rs")
            );
        Account mockAccount = AccountObjectMother.generateBasicFromAccount();
        mockAccount.setId(accountId);
        mockAccount.setAccountNumber(accountNumber);
        mockAccount.setClient(user);
        mockAccount.setEmployee(employee);
        final var account = accountRepo.saveAndFlush(mockAccount);
        Exchange exchange = exchangeRepository.save(TestDataFactory.buildExchange());
        Asset asset = assetRepository.save(TestDataFactory.buildAsset());
        actuaryRepository.save(TestDataFactory.buildActuaryInfo(user));
        listingRepository.save(TestDataFactory.buildListing(asset, exchange));
        orderRepository.save(TestDataFactory.buildOrder(user, account));
    }

    @Test
    void shouldCreateOrder() {
        CreateOrderDto dto =
            new CreateOrderDto(
                assetId,
                Direction.BUY,
                2,
                new MonetaryAmount(BigDecimal.valueOf(950), CurrencyCode.RSD),
                null,
                false,
                false,
                accountNumber
            );

        OrderDto response =
            orderService.createOrder(
                dto,
                userUuid,
                new AuthenticatedBankUserAuthentication(
                    new AuthenticatedBankUserPrincipal(UserType.CLIENT, userUuid),
                    "",
                    EnumSet.noneOf(Privilege.class)
                )
            );

        assertThat(response).isNotNull();
        assertThat(
            response.pricePerUnit()
                .getAmount()
        ).isEqualTo(BigDecimal.valueOf(950));
        assertThat(response.assetTicker()).isEqualTo("TST");
    }

    @Test
    void shouldCalculateAveragePrice() {
        CreateOrderPreviewDto dto =
            new CreateOrderPreviewDto(
                assetId,
                3,
                new MonetaryAmount(BigDecimal.valueOf(900), CurrencyCode.RSD),
                null,
                false,
                false,
                Direction.BUY
            );

        OrderPreviewDto response = orderService.calculateAveragePrice(dto);

        assertThat(response).isNotNull();
        assertThat(response.approximatePrice()).isEqualTo(BigDecimal.valueOf(2700));
        assertThat(response.orderType()).contains("Limit Order");
    }

    @Test
    void shouldReturnOrderByIdFromService() {
        OrderDto foundOrder = orderService.getOrderById(TestDataFactory.ORDER_ID);

        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.id()).isEqualTo(TestDataFactory.ORDER_ID);
        assertThat(foundOrder.orderType()).isEqualTo(OrderType.MARKET);
        assertThat(foundOrder.status()).isEqualTo(Status.PENDING);
        assertThat(foundOrder.assetTicker()).isEqualTo("TST");
        assertThat(foundOrder.quantity()).isEqualTo(100);
    }

}
