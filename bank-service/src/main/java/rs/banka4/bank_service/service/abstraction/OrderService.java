package rs.banka4.bank_service.service.abstraction;


import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rs.banka4.bank_service.domain.orders.db.Status;
import rs.banka4.bank_service.domain.orders.dtos.CreateOrderDto;
import rs.banka4.bank_service.domain.orders.dtos.CreateOrderPreviewDto;
import rs.banka4.bank_service.domain.orders.dtos.OrderDto;
import rs.banka4.bank_service.domain.orders.dtos.OrderPreviewDto;
import rs.banka4.rafeisen.common.security.AuthenticatedBankUserAuthentication;

public interface OrderService {

    OrderDto createOrder(
        CreateOrderDto request,
        UUID userId,
        AuthenticatedBankUserAuthentication authentication
    );

    OrderPreviewDto calculateAveragePrice(CreateOrderPreviewDto request);

    OrderDto getOrderById(UUID orderId);

    Page<OrderDto> searchOrders(List<Status> statuses, Pageable pageable);

    void acceptOrder(UUID orderId);

    void declineOrder(UUID orderId);

    void executeOrders();

    void updateOrderStatus(
        UUID orderId,
        Status newStatus,
        AuthenticatedBankUserAuthentication auth
    );
}
