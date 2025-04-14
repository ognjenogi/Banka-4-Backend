package rs.banka4.stock_service.unit.order;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import rs.banka4.stock_service.domain.orders.db.Order;
import rs.banka4.stock_service.domain.orders.db.Status;
import rs.banka4.stock_service.domain.orders.dtos.OrderDto;
import rs.banka4.stock_service.domain.orders.mapper.OrderMapper;
import rs.banka4.stock_service.exceptions.OrderNotFound;
import rs.banka4.stock_service.generator.OrderObjectMother;
import rs.banka4.stock_service.repositories.OrderRepository;
import rs.banka4.stock_service.service.impl.OrderServiceImpl;

public class OrderServiceSearchOrderTests {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchOrdersWithStatuses() {
        // Arrange
        List<Status> statuses = List.of(Status.APPROVED, Status.PENDING);
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orders =
            List.of(OrderObjectMother.generateBasicOrder(), OrderObjectMother.generateBasicOrder());
        Page<Order> orderPage = new PageImpl<>(orders);

        when(orderRepository.findAllByStatusIn(statuses, pageable)).thenReturn(orderPage);

        // Act
        Page<OrderDto> result = orderService.searchOrders(statuses, pageable);

        // Assert
        verify(orderRepository, times(1)).findAllByStatusIn(statuses, pageable);
        verify(orderRepository, never()).findAll(pageable);
        assert result.getContent()
            .size()
            == orders.size();
    }

    @Test
    void testSearchOrdersWithoutStatuses() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orders =
            List.of(OrderObjectMother.generateBasicOrder(), OrderObjectMother.generateBasicOrder());
        Page<Order> orderPage = new PageImpl<>(orders);

        when(orderRepository.findAll(pageable)).thenReturn(orderPage);

        // Act
        Page<OrderDto> result = orderService.searchOrders(null, pageable);

        // Assert
        verify(orderRepository, times(1)).findAll(pageable);
        verify(orderRepository, never()).findAllByStatusIn(anyList(), eq(pageable));
        assert result.getContent()
            .size()
            == orders.size();
    }

    @Test
    void testGetOrderByIdSuccess() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = OrderObjectMother.generateBasicOrder();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(
            OrderObjectMother.generateBasicOrderDto(order.getOrderType())
        );

        // Act
        OrderDto result = orderService.getOrderById(orderId);

        // Assert
        verify(orderRepository, times(1)).findById(orderId);
        assert result != null;
    }

    @Test
    void testGetOrderByIdNotFound() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFound.class, () -> orderService.getOrderById(orderId));

        // Verify
        verify(orderRepository, times(1)).findById(orderId);
    }

}
