package rs.banka4.bank_service.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import rs.banka4.bank_service.domain.orders.db.Status;
import rs.banka4.bank_service.domain.orders.dtos.CreateOrderDto;
import rs.banka4.bank_service.domain.orders.dtos.CreateOrderPreviewDto;
import rs.banka4.bank_service.domain.orders.dtos.OrderDto;
import rs.banka4.bank_service.domain.orders.dtos.OrderPreviewDto;

public interface OrderApiDocumentation {

    @Operation(
        summary = "Create an order (buy/sell request)",
        description = """
            Creates a new trading order for a specific financial instrument.
            The order can be of type BUY or SELL, depending on the direction.

            If the order exceeds the trader's daily limit, or if approval is required,
            it will need to be reviewed by a supervisor.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order successfully created",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OrderDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content
        )
    })
    ResponseEntity<OrderDto> createOrder(
        @RequestBody(
            description = "Payload containing order creation details",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateOrderDto.class))
        ) @Valid CreateOrderDto request,
        Authentication auth
    );

    @Operation(
        summary = "Calculate average price for an order preview",
        description = """
            Calculates the average price for a given order preview request.
            This can be used to estimate the cost or proceeds of an order before it is placed.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Average price successfully calculated",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OrderPreviewDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content
        )
    })
    ResponseEntity<OrderPreviewDto> calculateAveragePrice(
        @RequestBody(
            description = "Payload containing order preview details",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateOrderPreviewDto.class))
        ) @Valid CreateOrderPreviewDto request,
        Authentication auth
    );

    @Operation(
        summary = "Search orders with optional filters and pagination",
        description = """
            Retrieves a paginated list of orders based on the provided filters.
            Users can filter orders by their statuses and specify pagination parameters.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the list of orders",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content
        )
    })
    ResponseEntity<Page<OrderDto>> searchOrders(
        @RequestParam(required = false) List<Status> statuses,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    );

    @Operation(
        summary = "Retrieve a specific order by ID",
        description = """
            Returns the details of a specific order based on its unique identifier.
            This includes information such as quantity, price, direction (BUY/SELL), status, timestamps, etc.

            If no order is found with the given ID, a 404 Not Found response will be returned.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order found and returned",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OrderDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Unexpected server error",
            content = @Content
        )
    })
    ResponseEntity<OrderDto> getOrderById(
        @Parameter(
            description = "UUID of the order to retrieve",
            required = true,
            example = "d290f1ee-6c54-4b01-90e6-d701748f0851"
        ) @PathVariable UUID id
    );

    @Operation(
        summary = "Accept an order",
        description = """
            Approves the specified order.
            This action is typically performed by a supervisor or authorized personnel.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order successfully accepted",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Unexpected server error",
            content = @Content
        )
    })
    ResponseEntity<Void> acceptOrder(
        @Parameter(
            description = "UUID of the order to accept",
            required = true,
            example = "d290f1ee-6c54-4b01-90e6-d701748f0851"
        ) @PathVariable String orderId
    );

    @Operation(
        summary = "Decline an order",
        description = """
            Rejects the specified order.
            This action is typically performed by a supervisor or authorized personnel.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order successfully declined",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Unexpected server error",
            content = @Content
        )
    })
    ResponseEntity<Void> declineOrder(
        @Parameter(
            description = "UUID of the order to decline",
            required = true,
            example = "d290f1ee-6c54-4b01-90e6-d701748f0851"
        ) @PathVariable String orderId
    );

}
