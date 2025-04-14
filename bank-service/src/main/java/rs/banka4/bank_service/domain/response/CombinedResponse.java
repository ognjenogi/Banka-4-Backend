package rs.banka4.bank_service.domain.response;


import rs.banka4.rafeisen.common.dto.EmployeeResponseDto;

public record CombinedResponse(
    EmployeeResponseDto user,
    ActuaryInfoDto actuary
) {
}
