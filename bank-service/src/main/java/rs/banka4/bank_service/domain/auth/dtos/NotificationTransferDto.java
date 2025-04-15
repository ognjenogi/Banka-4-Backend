package rs.banka4.bank_service.domain.auth.dtos;

import java.util.Map;

public record NotificationTransferDto(
    String topic,
    String recipient,
    Map<String, Object> params
) {
}
