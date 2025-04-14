package rs.banka4.bank_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.banka4.bank_service.controller.docs.ExchangeApiDocumentation;
import rs.banka4.bank_service.domain.exchanges.dtos.ExchangeDto;
import rs.banka4.bank_service.service.abstraction.ExchangeService;

@RestController
@RequestMapping("/stock/exchanges")
@RequiredArgsConstructor
public class ExchangeController implements ExchangeApiDocumentation {

    // Note: The @Primary annotation in the mock service should be deleted.
    private final ExchangeService exchangeService;

    @Override
    @GetMapping
    public ResponseEntity<Page<ExchangeDto>> getAllExchanges(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return exchangeService.getAllExchanges(PageRequest.of(page, size));
    }
}
