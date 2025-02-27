package rs.banka4.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import rs.banka4.user_service.models.Privilege;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record CreateEmployeeDto(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Username is required")
        String username,

        @NotNull(message = "Date of birth is required")
        LocalDate dateOfBirth,

        @NotBlank(message = "Gender is required")
        String gender,

        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotBlank(message = "Address is required")
        String address,

        @NotNull(message = "Privileges are required")
        List<String> privilege,

        @NotBlank(message = "Position is required")
        String position,

        @NotBlank(message = "Department is required")
        String department,

        @NotNull(message = "Active is required")
        boolean active
) {
}
