package rs.banka4.stock_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.banka4.stock_service.exceptions.OptionNotFound;
import rs.banka4.stock_service.repositories.OptionsRepository;
import rs.banka4.stock_service.service.abstraction.OptionService;
import rs.banka4.stock_service.service.abstraction.TradingService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {
    private final OptionsRepository optionsRepository;
    private final TradingService tradingService;
    @Override
    public void buyOption(UUID optionId, UUID userId, String accountNumber) {
        //TODO check if account number is user's account and if he has enough money on it
        var option = optionsRepository.findById(optionId);
        if(option.isEmpty() || !option.get().isActive()){
            throw new OptionNotFound();
        }
        tradingService.buyOption(option.get(), userId, accountNumber);
    }

    @Override
    public void useOption(UUID optionId, UUID userId) {

    }
}
