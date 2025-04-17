package rs.banka4.bank_service.service.abstraction;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import rs.banka4.bank_service.domain.taxes.db.dto.TaxableUserDto;

public interface TaxService {
    Page<TaxableUserDto> getTaxSummary(String firstName, String lastName, PageRequest of);

    void taxUser(UUID userId);

    void taxMonthly();
}
