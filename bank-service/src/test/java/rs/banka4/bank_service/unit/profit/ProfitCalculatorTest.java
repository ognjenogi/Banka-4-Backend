package rs.banka4.bank_service.unit.profit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.options.db.Asset;
import rs.banka4.bank_service.domain.orders.db.Direction;
import rs.banka4.bank_service.domain.orders.db.Order;
import rs.banka4.bank_service.domain.orders.db.OrderType;
import rs.banka4.bank_service.domain.user.User;
import rs.banka4.bank_service.domain.user.client.db.Client;
import rs.banka4.bank_service.repositories.OrderRepository;
import rs.banka4.bank_service.service.impl.ProfitCalculationServiceImpl;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

@ExtendWith(MockitoExtension.class)
class ProfitCalculatorTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ProfitCalculationServiceImpl profitCalculator;

    private final UUID userId = UUID.randomUUID();
    private final Asset fakeAsset = new Asset() {
        {
            setId(UUID.randomUUID());
            setTicker("FOO");
        }
    };
    private final CurrencyCode C = CurrencyCode.USD;
    private final User user = new Client();

    private Order buildOrder(
        Direction dir,
        int quantity,
        BigDecimal pricePerUnit,
        boolean isDone,
        OffsetDateTime createdAt
    ) {
        return Order.builder()
            .id(UUID.randomUUID())
            .user(user)
            .asset(fakeAsset)
            .orderType(OrderType.MARKET)
            .quantity(quantity)
            .remainingPortions(0)
            .pricePerUnit(new MonetaryAmount(pricePerUnit, C))
            .direction(dir)
            .status(
                isDone
                    ? rs.banka4.bank_service.domain.orders.db.Status.APPROVED
                    : rs.banka4.bank_service.domain.orders.db.Status.PENDING
            )
            .isDone(isDone)
            .createdAt(createdAt)
            .lastModified(createdAt)
            .contractSize(1)
            .remainingPortions(0)
            .afterHours(false)
            .limitValue(null)
            .stopValue(null)
            .allOrNothing(false)
            .margin(false)
            .account(null)
            .used(false)
            .build();
    }

    @BeforeEach
    void setup() {
        user.setId(userId);
    }

    @Test
    void fullSellMatchesSingleBuy() {
        // BUY 10 @ $10 => gross=100, fee= min(0.14*100=14,7)=7, cost basis=107
        Order buy =
            buildOrder(
                Direction.BUY,
                10,
                BigDecimal.valueOf(10),
                true,
                OffsetDateTime.now()
                    .minusHours(1)
            );
        // SELL same 10 @ $15 => gross=150, fee= min(0.14*150=21,7)=7, net=143
        Order sell =
            buildOrder(Direction.SELL, 10, BigDecimal.valueOf(15), true, OffsetDateTime.now());

        when(
            orderRepository.findByUserIdAndAssetAndDirectionAndIsDone(
                eq(userId),
                eq(fakeAsset),
                eq(Direction.BUY),
                eq(true)
            )
        ).thenReturn(List.of(buy));


        MonetaryAmount realized = profitCalculator.calculateRealizedProfitForSell(sell);

        // expected profit = sellNet(143) – buyCost(107) = 36
        assertEquals(
            0,
            realized.getAmount()
                .compareTo(BigDecimal.valueOf(36))
        );
        assertEquals(C, realized.getCurrency());
    }

    @Test
    void partialSellAcrossTwoBuys() {
        OffsetDateTime t0 =
            OffsetDateTime.now()
                .minusHours(2);
        OffsetDateTime t1 = t0.plusMinutes(10);
        OffsetDateTime t2 = t1.plusMinutes(10);

        // BUY #1: 10 @ $10 => cost=100+7=107
        Order buy1 = buildOrder(Direction.BUY, 10, BigDecimal.valueOf(10), true, t0);
        // BUY #2: 20 @ $12 => cost=240+ (0.14*240=33.6→min33.6,7)=247
        Order buy2 = buildOrder(Direction.BUY, 20, BigDecimal.valueOf(12), true, t1);
        // SELL 15 @ $14 => gross=210, fee=min(0.14*210=29.4,7)=7, net=203
        Order sell = buildOrder(Direction.SELL, 15, BigDecimal.valueOf(14), true, t2);

        when(
            orderRepository.findByUserIdAndAssetAndDirectionAndIsDone(
                eq(userId),
                eq(fakeAsset),
                eq(Direction.BUY),
                eq(true)
            )
        ).thenReturn(List.of(buy1, buy2));


        MonetaryAmount realized = profitCalculator.calculateRealizedProfitForSell(sell);

        /*
         * Matching 15 FIFO: • 10 from buy1 @ cost 107 • 5 from buy2 @ cost = (5/20)*247 = 61.75
         * total cost = 107 + 61.75 = 168.75 sellNet = 203 profit = 203 - 168.75 = 34.25
         */
        assertEquals(
            0,
            realized.getAmount()
                .compareTo(
                    BigDecimal.valueOf(34.25)
                        .setScale(2)
                )
        );
        assertEquals(C, realized.getCurrency());
    }
}
