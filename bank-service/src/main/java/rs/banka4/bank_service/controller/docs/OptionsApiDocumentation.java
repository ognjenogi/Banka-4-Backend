package rs.banka4.bank_service.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import rs.banka4.bank_service.domain.options.dtos.BuyOptionRequestDto;
import rs.banka4.bank_service.domain.options.dtos.UseOptionRequest;
import rs.banka4.bank_service.exceptions.*;

public interface OptionsApiDocumentation {
    @Operation(
        summary = "Buy option",
        description = "Buys a chosen option from Exchange and stores it in my portfolio",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully bought an option"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "OptionNotFound",
                content = @Content(schema = @Schema(implementation = OptionNotFound.class))
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden"
            ),
            @ApiResponse(
                responseCode = "503",
                description = "Transaction service not implemented yet"
            )
        }
    )
    void buyOption(@RequestBody BuyOptionRequestDto buyOptionRequestDto, Authentication auth);

    @Operation(
        summary = "Use the owned option",
        description = "Uses the owned option, so that you can buy stock for it's price if it is CALL, or sell stock if it is PUT",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully bought an option"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "OptionNotFound",
                content = @Content(schema = @Schema(implementation = OptionNotFound.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "OptionOwnershipNotFound - user doesn't own this option",
                content = @Content(schema = @Schema(implementation = OptionOwnershipNotFound.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "StockOwnershipNotFound - user doesn't own this stock",
                content = @Content(schema = @Schema(implementation = StockOwnershipNotFound.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "OptionExpired",
                content = @Content(schema = @Schema(implementation = OptionExpired.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "NotEnoughStock - there is not enough stock on user's account to use the ",
                content = @Content(schema = @Schema(implementation = NotEnoughStock.class))
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden"
            ),
            @ApiResponse(
                responseCode = "503",
                description = "Transaction service not implemented yet"
            )
        }
    )
    void useOption(@RequestBody UseOptionRequest useOptionRequest, Authentication auth);
}
