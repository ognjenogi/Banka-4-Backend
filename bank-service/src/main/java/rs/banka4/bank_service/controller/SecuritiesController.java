package rs.banka4.bank_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.banka4.bank_service.controller.docs.SecuritiesApiDocumentation;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.security.SecurityDto;
import rs.banka4.bank_service.domain.security.responses.SecurityHoldingDto;
import rs.banka4.bank_service.service.abstraction.SecuritiesService;
import rs.banka4.rafeisen.common.security.AuthenticatedBankUserAuthentication;

@RestController
@RequestMapping("/stock/securities")
@RequiredArgsConstructor
public class SecuritiesController implements SecuritiesApiDocumentation {

    private final SecuritiesService securityService;

    @Override
    @GetMapping
    public ResponseEntity<Page<SecurityDto>> getSecurities(
        @RequestParam(required = false) String securityType,
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return securityService.getSecurities(securityType, name, PageRequest.of(page, size));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<SecurityHoldingDto>> getMyPortfolio(
        Authentication auth,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        final var ourAuth = (AuthenticatedBankUserAuthentication) auth;
        var myId =
            ourAuth.getPrincipal()
                .userId();
        var holdings = securityService.getMyPortfolio(myId, PageRequest.of(page, size));
        return ResponseEntity.ok(holdings);
    }

    @GetMapping("/profit")
    public ResponseEntity<MonetaryAmount> getMyProfit(Authentication auth) {
        final var ourAuth = (AuthenticatedBankUserAuthentication) auth;
        var myId =
            ourAuth.getPrincipal()
                .userId();
        var amount = securityService.calculateTotalProfit(myId);
        return ResponseEntity.ok(amount);
    }
}
