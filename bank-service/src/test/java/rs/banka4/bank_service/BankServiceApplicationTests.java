package rs.banka4.bank_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import rs.banka4.bank_service.service.impl.ExchangeRateService;
import rs.banka4.testlib.integration.DbEnabledTest;

@SpringBootTest
@DbEnabledTest
class BankServiceApplicationTests {
    /* Requires a property we don't set. */
    @MockitoBean
    ExchangeRateService exchRateService;

    @Test
    void contextLoads() {
    }

}
