package rs.banka4.bank_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rs.banka4.bank_service.domain.exchanges.dtos.ExchangeDto;
import rs.banka4.bank_service.service.abstraction.ExchangeService;

@Service
public class ExchangeServiceImpl implements ExchangeService {
    @Override
    public ResponseEntity<Page<ExchangeDto>> getAllExchanges(Pageable pageable) {
        return null;
    }
}
