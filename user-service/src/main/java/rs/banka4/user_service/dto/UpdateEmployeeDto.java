package rs.banka4.user_service.dto;

import jakarta.validation.constraints.Email;


import java.time.LocalDate;
import java.util.List;


public record UpdateEmployeeDto(
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String gender,
    @Email
    String email,
    String username,
    String phone,
    String address,
    List<String> privilege,
    String position,
    String department,
    boolean active
) {
}