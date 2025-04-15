package rs.banka4.bank_service.domain.card.db;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import lombok.Builder;
import rs.banka4.rafeisen.common.dto.Gender;

@Embeddable
@Builder
public record AuthorizedUser(
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String email,
    String phoneNumber,
    String address,
    @Enumerated(EnumType.STRING) Gender gender
) {
}
