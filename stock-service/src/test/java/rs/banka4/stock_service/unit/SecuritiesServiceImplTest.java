package rs.banka4.stock_service.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.sun.security.auth.UserPrincipal;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import rs.banka4.stock_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.stock_service.domain.orders.db.Direction;
import rs.banka4.stock_service.domain.orders.db.Order;
import rs.banka4.stock_service.domain.security.Security;
import rs.banka4.stock_service.domain.security.forex.db.CurrencyCode;
import rs.banka4.stock_service.domain.security.forex.db.ForexPair;
import rs.banka4.stock_service.domain.security.responses.SecurityOwnershipResponse;
import rs.banka4.stock_service.domain.security.stock.db.Stock;
import rs.banka4.stock_service.repositories.OrderRepository;
import rs.banka4.stock_service.service.abstraction.ListingService;
import rs.banka4.stock_service.service.impl.SecuritiesServiceImpl;

@ExtendWith(MockitoExtension.class)
public class SecuritiesServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ListingService listingService;

    @InjectMocks
    private SecuritiesServiceImpl service;

    private final UUID userId = UUID.randomUUID();
    private final Security stock =
        Stock.builder()
            .id(UUID.randomUUID())
            .ticker("AAPL")
            .build();
    private final Security forex =
        ForexPair.builder()
            .id(UUID.randomUUID())
            .exchangeRate(new BigDecimal("1.18"))
            .build();

    @Test
    public void getMySecurities_shouldReturnEmptyListWhenNoOrders() {
        // Given
        when(orderRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        mockSecurityContext(userId);

        // When
        List<SecurityOwnershipResponse> result = service.getMySecurities();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    public void getMySecurities_shouldMapStockSecurityCorrectly() {
        // Given
        Order order = createOrder(stock, 100, "150.00", Direction.BUY);
        mockSecurityContext(userId);

        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order));
        when(listingService.getListingDetails(stock.getId()));
        when(
            orderRepository.findByUserIdAndAssetAndDirectionAndIsDone(
                userId,
                stock,
                Direction.BUY,
                true
            )
        ).thenReturn(List.of(order));

        // When
        List<SecurityOwnershipResponse> result = service.getMySecurities();

        // Then
        assertThat(result).hasSize(1);
        SecurityOwnershipResponse response = result.get(0);
        assertThat(response.type()).isEqualTo("Stock");
        assertThat(response.ticker()).isEqualTo(stock.getTicker());
        assertThat(response.amount()).isEqualTo(100);
        assertThat(response.price()).isEqualTo(new BigDecimal("172.50"));
        assertThat(response.profit()).isEqualTo(new BigDecimal("2250.00")); // (172.50 - 150.00) *
                                                                            // 100
    }

    @Test
    public void getMySecurities_shouldCalculateForexPriceFromExchangeRate() {
        // Given
        Order order = createOrder(forex, 5000, "1.15", Direction.BUY);
        mockSecurityContext(userId);

        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order));
        when(
            orderRepository.findByUserIdAndAssetAndDirectionAndIsDone(
                userId,
                forex,
                Direction.BUY,
                true
            )
        ).thenReturn(List.of(order));

        // When
        List<SecurityOwnershipResponse> result = service.getMySecurities();

        // Then
        assertThat(
            result.get(0)
                .price()
        ).isEqualTo(new BigDecimal("1.18"));
    }


    @Test
    public void getMySecurities_shouldCalculateAverageCostFromMultipleBuys() {
        // Given
        Order buy1 = createOrder(stock, 50, "140.00", Direction.BUY);
        Order buy2 = createOrder(stock, 30, "160.00", Direction.BUY);
        Order sell = createOrder(stock, 20, "170.00", Direction.SELL);
        mockSecurityContext(userId);

        when(orderRepository.findByUserId(userId)).thenReturn(List.of(buy1, buy2, sell));
        when(listingService.getListingDetails(stock.getId()));
        when(
            orderRepository.findByUserIdAndAssetAndDirectionAndIsDone(
                userId,
                stock,
                Direction.BUY,
                true
            )
        ).thenReturn(List.of(buy1, buy2));

        // When
        List<SecurityOwnershipResponse> result = service.getMySecurities();

        // Then
        assertThat(
            result.get(0)
                .amount()
        ).isEqualTo(60); // 50 + 30 - 20
        BigDecimal expectedAvgCost = new BigDecimal("147.50"); // (50*140 + 30*160)/80
        BigDecimal expectedProfit = new BigDecimal("1950.00"); // (180 - 147.50) * 60
        assertThat(
            result.get(0)
                .profit()
        ).isEqualTo(expectedProfit);
    }

    @Test
    public void getMySecurities_shouldReturnZeroProfitWhenNoBuyOrders() {
        // Given
        Order sellOrder = createOrder(stock, 20, "170.00", Direction.SELL);
        mockSecurityContext(userId);

        when(orderRepository.findByUserId(userId)).thenReturn(List.of(sellOrder));
        when(listingService.getListingDetails(stock.getId()));
        when(
            orderRepository.findByUserIdAndAssetAndDirectionAndIsDone(
                userId,
                stock,
                Direction.BUY,
                true
            )
        ).thenReturn(Collections.emptyList());

        // When
        List<SecurityOwnershipResponse> result = service.getMySecurities();

        // Then
        assertThat(
            result.get(0)
                .profit()
        ).isEqualTo(BigDecimal.ZERO);
    }

    private Order createOrder(Security security, int quantity, String price, Direction direction) {
        return Order.builder()
            .userId(userId)
            .asset(security)
            .quantity(quantity)
            .pricePerUnit(new MonetaryAmount(new BigDecimal(price), CurrencyCode.USD))
            .direction(direction)
            .isDone(true)
            .lastModified(OffsetDateTime.now())
            .build();
    }

    private void mockSecurityContext(UUID userId) {
        Authentication auth =
            new UsernamePasswordAuthenticationToken(
                new UserPrincipal("test"),
                null,
                Collections.emptyList()
            );
        SecurityContextHolder.getContext()
            .setAuthentication(auth);
    }

    // Helper record for listing details
    private record ListingDetails(BigDecimal price) {
    }
}
