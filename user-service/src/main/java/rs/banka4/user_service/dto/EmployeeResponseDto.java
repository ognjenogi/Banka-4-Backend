package rs.banka4.user_service.dto;

import rs.banka4.user_service.models.Privilege;

import java.time.LocalDate;
import java.util.EnumSet;

public record EmployeeResponseDto(
        String id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String email,
        String phone,
        String address,
        String username,
        String position,
        String department,
        EnumSet<Privilege> privileges,
        boolean active
) {}