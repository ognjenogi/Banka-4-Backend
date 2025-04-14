package rs.banka4.bank_service.service.abstraction;

import java.util.UUID;

public interface OptionService {
    void buyOption(UUID optionId, UUID userId, String accountNumber, int amount);

    void useOption(UUID optionId, UUID userId, String accountNumber);
}
