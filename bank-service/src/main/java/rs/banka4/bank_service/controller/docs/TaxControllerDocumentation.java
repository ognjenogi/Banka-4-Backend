package rs.banka4.bank_service.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import rs.banka4.bank_service.domain.taxes.db.dto.TaxableUserDto;

public interface TaxControllerDocumentation {
    @Operation(
        summary = "Manual Trigger for All Users",
        description = "Triggers monthly capital gains tax processing for all users. "
            + "Used by supervisors to initiate end‐of‐month tax deductions manually.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully triggered monthly tax for all users"
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden – insufficient permissions"
            )
        }
    )
    ResponseEntity<Void> triggerMonthlyTax();

    @Operation(
        summary = "Manual Payment for a Specific User",
        description = "Immediately charges the current unpaid capital gains tax for the specified user. "
            + "Used in edge cases where individual tax processing is needed (e.g. recovery, correction).",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully collected tax for the user"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "User not found"
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden – insufficient permissions"
            )
        }
    )
    ResponseEntity<Void> collectTaxForUser(
        @Parameter(
            in = ParameterIn.PATH,
            description = "ID of the user to collect tax for",
            required = true
        ) @PathVariable UUID userId
    );

    @Operation(
        summary = "Get List of All Taxable Users",
        description = "Returns a list of all users who are eligible for capital gains tax.  "
            + "Each entry includes the user’s ID, role, name, and current unpaid tax amount in RSD.  "
            + "Supports optional filters by first name and last name (partial, case‐insensitive).",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved list of taxable users",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TaxableUserDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden"
            )
        }
    )
    ResponseEntity<Page<TaxableUserDto>> getTaxSummary(
        @Parameter(
            in = ParameterIn.QUERY,
            description = "Filter by first name (partial match)"
        ) @RequestParam(required = false) String firstName,
        @Parameter(
            in = ParameterIn.QUERY,
            description = "Filter by last name (partial match)"
        ) @RequestParam(required = false) String lastName,
        int page,
        int size
    );
}
