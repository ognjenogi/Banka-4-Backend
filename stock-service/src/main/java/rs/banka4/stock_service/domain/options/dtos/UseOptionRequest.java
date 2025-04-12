package rs.banka4.stock_service.domain.options.dtos;

import java.util.UUID;

public record UseOptionRequest(
    UUID optionId,
    String accountNumber
) {
}
