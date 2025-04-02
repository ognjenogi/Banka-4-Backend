package rs.banka4.stock_service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import rs.banka4.stock_service.repositories.AssetRepository;
import rs.banka4.stock_service.repositories.ExchangeRepository;
import rs.banka4.stock_service.repositories.ListingDailyPriceInfoRepository;
import rs.banka4.stock_service.repositories.ListingRepository;
import rs.banka4.stock_service.repositories.SecurityRepository;
import rs.banka4.stock_service.utils.AssetGenerator;
import rs.banka4.stock_service.utils.ExchangeGenerator;
import rs.banka4.stock_service.utils.ListingGenerator;
import rs.banka4.testlib.integration.DbEnabledTest;

@SpringBootTest
@DbEnabledTest
@AutoConfigureMockMvc
public class ListingTests {
    @Autowired
    private MockMvcTester mvc;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private SecurityRepository securityRepository;
    @Autowired
    private ListingRepository listingRepo;
    @Autowired
    private ExchangeRepository exchangeRepo;
    @Autowired
    private ListingDailyPriceInfoRepository listingHistoryRepo;

    @Test
    public void test_getListings_no_filter() {
        final var ber1 = ExchangeGenerator.makeBer1();
        exchangeRepo.save(ber1);
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        ListingGenerator.makeExampleListings(
            ex1.orElseThrow(),
            ber1,
            listingRepo,
            listingHistoryRepo
        );

        mvc.get()
            /* TODO(arsen): swap with security id? */
            .uri("/listings/{id}", "5b0d1174-2894-49ed-8648-6ba9d3faa2ca")
            .assertThat()
            .bodyJson()
            .isLenientlyEqualTo("""
                {
                  "securityType": "STOCK",
                  "name": "Example One™",
                  "ticker": "EX1",
                  "change": 21.96,
                  "price": 4.57,
                  "outstandingShares": 325000,
                  "dividendYield": 0.05
                }
                """);
    }
}
