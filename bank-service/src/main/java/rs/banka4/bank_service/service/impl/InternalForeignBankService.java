package rs.banka4.bank_service.service.impl;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.banka4.bank_service.domain.trading.db.ForeignBankId;
import rs.banka4.bank_service.domain.user.User;
import rs.banka4.bank_service.repositories.UserRepository;
import rs.banka4.bank_service.service.abstraction.ForeignBankService;

/**
 * A {@link ForeignBankService} that does not interact with any foreign bank.
 */
@Service
@RequiredArgsConstructor
public class InternalForeignBankService implements ForeignBankService {
    private final UserRepository userRepository;

    @Override
    public Optional<String> getUsernameFor(ForeignBankId foreignBankId) {
        if (foreignBankId.routingNumber() != ForeignBankId.OUR_ROUTING_NUMBER)
            throw new IllegalArgumentException(
                "Cannot handle foreign foreign bank IDs at this time"
            );

        try {
            return userRepository.findById(UUID.fromString(foreignBankId.userId()))
                .map(User::getEmail);
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}
