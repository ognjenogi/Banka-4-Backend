package rs.banka4.stock_service.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import rs.banka4.stock_service.domain.listing.dtos.ListingDto;
import rs.banka4.stock_service.domain.listing.dtos.ListingInfoDto;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;
import rs.banka4.stock_service.domain.trading.db.dtos.OtcRequestDto;

public interface OtcApiDocumentation {
    @Operation(
        summary = "Get My Requests",
        description = "Retrieves negotiations in which the authenticated user participates, either as the initiator or the recipient.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved negotiations",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = OtcRequestDto.class))
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters"
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden"
            )
        }
    )
    Page<ListingInfoDto> getMyRequests(
        @Parameter(description = "Page number") int page,
        @Parameter(description = "Number of listings per page") int size,
        Authentication auth
    );

    @Operation(
        summary = "Get My Unread Requests",
        description = "Retrieves unread negotiations (those where the last modification was not done by the authenticated user). " +
            "This enables the user to see the negotiations in which a response or further action is pending.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved unread negotiations",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = OtcRequestDto.class))
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters"
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden"
            )
        }
    )
    Page<ListingInfoDto> getMyRequestsUnread(
        @Parameter(description = "Page number") int page,
        @Parameter(description = "Number of listings per page") int size,
        Authentication auth
    );
}
