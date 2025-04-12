package rs.banka4.stock_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rs.banka4.rafeisen.common.security.AuthenticatedBankUserAuthentication;
import rs.banka4.stock_service.domain.options.dtos.BuyRequestDto;
import rs.banka4.stock_service.service.abstraction.OptionService;

import java.util.UUID;

@RestController
@RequestMapping("/options")
@RequiredArgsConstructor
public class OptionsController {
    private final OptionService optionService;

    @GetMapping("/buy")
    public void buyOption (@RequestBody BuyRequestDto buyRequestDto, Authentication auth){
        UUID userId = ((AuthenticatedBankUserAuthentication) auth).getPrincipal().userId();
        optionService.buyOption(buyRequestDto.optionId(), userId, buyRequestDto.accountNumber());
    }
}
