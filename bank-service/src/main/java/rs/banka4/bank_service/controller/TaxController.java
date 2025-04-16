package rs.banka4.bank_service.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.banka4.bank_service.controller.docs.TaxControllerDocumentation;
import rs.banka4.bank_service.domain.taxes.db.dto.TaxableUserDto;
import rs.banka4.bank_service.service.abstraction.TaxService;

@RestController
@RequestMapping("/stock/tax")
@RequiredArgsConstructor
public class TaxController implements TaxControllerDocumentation {
    private final TaxService taxService;

    @PostMapping("/trigger-monthly")
    @Override
    public ResponseEntity<Void> triggerMonthlyTax() {
        taxService.taxMonthly();
        return ResponseEntity.ok()
            .build();
    }

    @PostMapping("/collect/{userId}")
    @Override
    public ResponseEntity<Void> collectTaxForUser(@PathVariable UUID userId) {
        taxService.taxUser(userId);
        return ResponseEntity.ok()
            .build();
    }

    @GetMapping("/summary")
    @Override
    public ResponseEntity<Page<TaxableUserDto>> getTaxSummary(
        @RequestParam(required = false) String firstName,
        @RequestParam(required = false) String lastName,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        var res = taxService.getTaxSummary(firstName, lastName, PageRequest.of(page, size));
        return ResponseEntity.ok(res);
    }
}
