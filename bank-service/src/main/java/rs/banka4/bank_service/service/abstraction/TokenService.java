package rs.banka4.bank_service.service.abstraction;

import java.util.Optional;
import rs.banka4.bank_service.domain.auth.db.Token;

public interface TokenService {
    void invalidateToken(String token);

    Optional<Token> findByToken(String token);
}
