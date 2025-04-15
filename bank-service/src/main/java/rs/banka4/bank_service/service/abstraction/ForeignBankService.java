package rs.banka4.bank_service.service.abstraction;

import java.util.Optional;
import rs.banka4.bank_service.domain.trading.db.ForeignBankId;

/**
 * A service for all things related to resolving foreign bank identifiers.
 */
public interface ForeignBankService {
    Optional<String> getUsernameFor(ForeignBankId foreignBankId);
}
