package rs.banka4.bank_service.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import rs.banka4.bank_service.domain.exchange.dtos.ExchangeRateDto;
import rs.banka4.bank_service.service.abstraction.ExchangeRateService;

@Service
/* Because we can't reach out to other services in tests. */
@Profile("!test")
public class ExchangeRateServiceImpl extends ExchangeRateService {
    private final RestTemplate restTemplate;

    public ExchangeRateServiceImpl(@Value("${services.exchange}") String exchangeBaseUrl) {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(exchangeBaseUrl));
    }

    @Override
    public ExchangeRateDto getExchangeRates() {
        return restTemplate.exchange("/exchange-rate", HttpMethod.GET, null, ExchangeRateDto.class)
            .getBody();
    }
}
