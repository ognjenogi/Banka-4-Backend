package rs.banka4.stock_service.domain.response;

import rs.banka4.stock_service.domain.actuaries.db.ActuaryInfo;

public record CombinedResponse(EmployeeResponseDto user,
                               ActuaryInfoDto actuary) {
}
