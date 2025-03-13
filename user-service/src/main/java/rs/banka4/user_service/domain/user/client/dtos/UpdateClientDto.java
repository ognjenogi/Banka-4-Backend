package rs.banka4.user_service.domain.user.client.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import rs.banka4.user_service.domain.user.Gender;
import rs.banka4.user_service.domain.user.Privilege;

import java.time.LocalDate;
import java.util.Set;

@Schema(description = "DTO for updating client details")
public record UpdateClientDto(

        @Schema(description = "First name", example = "Mehmedalija")
        String firstName,

        @Schema(description = "Last name", example = "Doe")
        String lastName,

        @Schema(description = "Date of birth", example = "1990-01-01")
        LocalDate dateOfBirth,

        @Schema(description = "Gender", example = "MALE")
        Gender gender,

        @Email(message = "Invalid email format.")
        @Schema(description = "Email address", example = "danny.jo@example.com")
        String email,

        @Schema(description = "Phone number", example = "123-456-7890")
        String phoneNumber,

        @Schema(description = "Address", example = "123 Main St")
        String address,

        @Schema(description = "Client's privileges", example = "[\"TRADE_STOCKS\", \"CONTRACTS\"]")
        Set<Privilege> privilege
) { }
