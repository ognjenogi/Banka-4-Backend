package rs.banka4.stock_service.service.abstraction;

import java.util.UUID;

public interface OptionService {
    void buyOption(UUID optionId, UUID userId, String accountNumber);

    void useOption(UUID optionId, UUID userId);
}
