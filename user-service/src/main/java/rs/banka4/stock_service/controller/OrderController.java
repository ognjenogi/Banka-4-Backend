package rs.banka4.stock_service.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rs.banka4.rafeisen.common.security.AuthenticatedBankUserAuthentication;
import rs.banka4.stock_service.controller.docs.OrderApiDocumentation;
import rs.banka4.stock_service.domain.orders.db.Status;
import rs.banka4.stock_service.domain.orders.dtos.CreateOrderDto;
import rs.banka4.stock_service.domain.orders.dtos.CreateOrderPreviewDto;
import rs.banka4.stock_service.domain.orders.dtos.OrderDto;
import rs.banka4.stock_service.domain.orders.dtos.OrderPreviewDto;
import rs.banka4.stock_service.service.abstraction.OrderService;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController implements OrderApiDocumentation {

    private final OrderService orderService;

    @Override
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
        @RequestBody @Valid CreateOrderDto request,
        Authentication auth
    ) {
        final var ourAuth = (AuthenticatedBankUserAuthentication) auth;
        return ResponseEntity.ok(
            orderService.createOrder(
                request,
                ourAuth.getPrincipal()
                    .userId()
            )
        );
    }

    @Override
    @PostMapping("/calculate-average-price")
    public ResponseEntity<OrderPreviewDto> calculateAveragePrice(
        @RequestBody @Valid CreateOrderPreviewDto request,
        Authentication auth
    ) {
        return ResponseEntity.ok(orderService.calculateAveragePrice(request));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<OrderDto>> searchOrders(
        @RequestParam(required = false) List<Status> statuses,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.searchOrders(statuses, PageRequest.of(page, size)));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

}
