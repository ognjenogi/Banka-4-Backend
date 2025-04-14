package rs.banka4.bank_service.domain.options.dtos;

import java.util.UUID;

public record BuyOptionRequestDto(
    UUID optionId,
    String accountNumber,
    int amount
) {
}
