package rs.banka4.bank_service.service.abstraction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import rs.banka4.bank_service.domain.taxes.db.dto.TaxableUserDto;

import java.util.UUID;

public interface TaxService {
    Page<TaxableUserDto> getTaxSummary(String firstName, String lastName, PageRequest of);

    void taxUser(UUID userId);

    void taxMonthly();
}
