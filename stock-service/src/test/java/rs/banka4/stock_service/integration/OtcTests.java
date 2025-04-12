package rs.banka4.stock_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import retrofit2.Response;
import retrofit2.Retrofit;
import rs.banka4.rafeisen.common.currency.CurrencyCode;
import rs.banka4.rafeisen.common.dto.UserResponseDto;
import rs.banka4.stock_service.config.clients.UserServiceClient;
import rs.banka4.stock_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.stock_service.domain.security.stock.db.Stock;
import rs.banka4.stock_service.domain.trading.db.ForeignBankId;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;
import rs.banka4.stock_service.domain.trading.db.RequestStatus;
import rs.banka4.stock_service.domain.trading.db.dtos.OtcRequestUpdateDto;
import rs.banka4.stock_service.repositories.AssetRepository;
import rs.banka4.stock_service.repositories.OtcRequestRepository;
import rs.banka4.stock_service.repositories.SecurityRepository;
import rs.banka4.stock_service.utils.AssetGenerator;
import rs.banka4.testlib.integration.DbEnabledTest;
import rs.banka4.testlib.utils.JwtPlaceholders;

@SpringBootTest
@DbEnabledTest
@AutoConfigureMockMvc
public class OtcTests {

    @Autowired
    private MockMvcTester mvc;
    @Autowired
    private ObjectMapper objMapper;
    @MockitoBean
    private Retrofit userServiceRetrofit;

    @Autowired
    private OtcRequestRepository otcRequestRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private SecurityRepository securityRepository;

    @BeforeEach
    public void setup() throws IOException {
        UserServiceClient userServiceClientMock = Mockito.mock(UserServiceClient.class);
        @SuppressWarnings("unchecked")
        retrofit2.Call<UserResponseDto> dummyCall = Mockito.mock(retrofit2.Call.class);
        UserResponseDto dummyUserDto = new UserResponseDto("sd", "sdasd", "test@test.com");
        Response<UserResponseDto> dummyResponse = Response.success(dummyUserDto);
        Mockito.when(dummyCall.execute())
            .thenReturn(dummyResponse);
        Mockito.when(userServiceClientMock.getUserInfo(Mockito.any(), Mockito.anyString()))
            .thenReturn(dummyCall);
        Mockito.when(userServiceRetrofit.create(UserServiceClient.class))
            .thenReturn(userServiceClientMock);
    }

    @Test
    public void testGetMyRequests() {
        OtcRequest dummyRequest = createDummyOtcRequest();
        createDummyOtcRequestMeRead();
        String expectedJson = """
            {
              "content": [
                {
                  "stock": {
                    "Name": "Example One™",
                    "DividendYield": "0.05",
                    "SharesOutstanding": "325000",
                    "MarketCapitalization": null
                  },
                  "pricePerStock": {"amount": 1.00, "currency": "AUD"},
                  "premium": {"amount": 1.00, "currency": "AUD"},
                  "amount": 1,
                  "madeBy": "test@test.com",
                  "madeFor": "test@test.com",
                  "modifiedBy": "test@test.com",
                  "lastModifiedDate": null,
                  "settlementDate": "2025-04-11"
                },
                {
                }
              ],
              "page": {
                "size": 10,
                "number": 0,
                "totalElements": 2,
                "totalPages": 1
              }
            }
            """;
        var jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/otc/me")
            .param("page", "0")
            .param("size", "10")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);

    }

    @Test
    public void testGetMyRequestsFails() {
        OtcRequest dummyRequest = createDummyOtcRequestNotMe();

        mvc.get()
            .uri("/otc/me")
            .param("page", "0")
            .param("size", "10")
            .header("Authorization", "Bearer " + JwtPlaceholders.CLIENT_TOKEN)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .extractingPath("$.content")
            .asArray()
            .isEmpty();

    }

    @Test
    public void testGetMyRequestsUnread() {
        OtcRequest dummyRequest = createDummyOtcRequestMeRead();
        createDummyOtcRequestMeUnread();
        String expectedJson = """
            {
              "content": [
                {
                  "stock": {
                    "Name": "Example One™",
                    "DividendYield": "0.05",
                    "SharesOutstanding": "325000",
                    "MarketCapitalization": null
                  },
                  "pricePerStock": {"amount": 1.00, "currency": "AUD"},
                  "premium": {"amount": 1.00, "currency": "AUD"},
                  "amount": 1,
                  "madeBy": "test@test.com",
                  "madeFor": "test@test.com",
                  "modifiedBy": "test@test.com",
                  "lastModifiedDate": null,
                  "settlementDate": "2025-04-11"
                }
              ],
              "page": {
                "size": 10,
                "number": 0,
                "totalElements": 1,
                "totalPages": 1
              }
            }
            """;
        var jwtToken = "Bearer " + JwtPlaceholders.CLIENT_TOKEN;
        mvc.get()
            .uri("/otc/me/unread")
            .param("page", "0")
            .param("size", "10")
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(expectedJson);

    }

    @Test
    public void testGetMyRequestsUnreadFails() {
        OtcRequest dummyRequest = createDummyOtcRequestNotMe();
        createDummyOtcRequestMeRead();

        mvc.get()
            .uri("/otc/me/unread")
            .param("page", "0")
            .param("size", "10")
            .header("Authorization", "Bearer " + JwtPlaceholders.CLIENT_TOKEN)
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .extractingPath("$.content")
            .asArray()
            .isEmpty();

    }

    @Test
    public void testRejectOtc() {
        OtcRequest dummyRequest = createDummyOtcRequestNotMe();
        var id = dummyRequest.getId();
        createDummyOtcRequestMeRead();

        mvc.patch()
            .uri("/otc/reject/" + id)
            .header("Authorization", "Bearer " + JwtPlaceholders.CLIENT_TOKEN)
            .assertThat()
            .hasStatusOk();
        var otcRejected =
            otcRequestRepository.findById(id)
                .get();
        assertEquals(RequestStatus.REJECTED, otcRejected.getStatus());
    }
    @Test
    public void testUpdateOtc() throws JsonProcessingException {
        OtcRequest dummyRequest = createDummyOtcRequestMeRead();
        var id = dummyRequest.getId();
        var momo = new MonetaryAmount();
        momo.setAmount(BigDecimal.TEN);
        momo.setCurrency(CurrencyCode.CAD);
        var updateDto = new OtcRequestUpdateDto(null,momo,10,LocalDate.parse("2025-04-11"));
        var body = objMapper.writeValueAsString(updateDto);
        mvc.patch()
                .uri("/otc/update/" + id)
                .header("Authorization", "Bearer " + JwtPlaceholders.CLIENT_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .assertThat()
                .hasStatusOk();
        var otcRejected =
                otcRequestRepository.findById(id)
                        .get();
        assertEquals(dummyRequest.getPricePerStock().getCurrency(),otcRejected.getPricePerStock().getCurrency());
        assertEquals(momo.getCurrency(),otcRejected.getPremium().getCurrency());
        assertEquals(10,otcRejected.getAmount());
        assertEquals(JwtPlaceholders.CLIENT_ID.toString(),otcRejected.getModifiedBy().userId());
    }

    private OtcRequest createDummyOtcRequest() {
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        var momo = new MonetaryAmount();
        momo.setAmount(BigDecimal.ONE);
        momo.setCurrency(CurrencyCode.AUD);
        OtcRequest req = new OtcRequest();
        req.setStock((Stock) ex1.get());
        req.setMadeBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setMadeFor(new ForeignBankId(1L, JwtPlaceholders.CLIENT_ID.toString()));
        req.setModifiedBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setStatus(RequestStatus.ACTIVE);
        req.setPremium(momo);
        req.setPricePerStock(momo);
        req.setSettlementDate(LocalDate.parse("2025-04-11"));
        req.setPricePerStock(momo);
        req.setAmount(1);

        otcRequestRepository.save(req);
        return req;
    }

    private OtcRequest createDummyOtcRequestNotMe() {
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        var momo = new MonetaryAmount();
        momo.setAmount(BigDecimal.ONE);
        momo.setCurrency(CurrencyCode.AUD);
        OtcRequest req = new OtcRequest();
        req.setStock((Stock) ex1.get());
        req.setMadeBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setMadeFor(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setModifiedBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setStatus(RequestStatus.ACTIVE);
        req.setPremium(momo);
        req.setPricePerStock(momo);
        req.setSettlementDate(LocalDate.parse("2025-04-11"));
        req.setPricePerStock(momo);
        req.setAmount(1);

        otcRequestRepository.save(req);
        return req;
    }

    private OtcRequest createDummyOtcRequestMeUnread() {
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        var momo = new MonetaryAmount();
        momo.setAmount(BigDecimal.ONE);
        momo.setCurrency(CurrencyCode.AUD);
        OtcRequest req = new OtcRequest();
        req.setStock((Stock) ex1.get());
        req.setMadeBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setMadeFor(new ForeignBankId(1L, JwtPlaceholders.CLIENT_ID.toString()));
        req.setModifiedBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setStatus(RequestStatus.ACTIVE);
        req.setPremium(momo);
        req.setPricePerStock(momo);
        req.setSettlementDate(LocalDate.parse("2025-04-11"));
        req.setPricePerStock(momo);
        req.setAmount(1);

        otcRequestRepository.save(req);
        return req;
    }

    private OtcRequest createDummyOtcRequestMeRead() {
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        var momo = new MonetaryAmount();
        momo.setAmount(BigDecimal.ONE);
        momo.setCurrency(CurrencyCode.AUD);
        OtcRequest req = new OtcRequest();
        req.setStock((Stock) ex1.get());
        req.setMadeBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setMadeFor(new ForeignBankId(1L, JwtPlaceholders.CLIENT_ID.toString()));
        req.setModifiedBy(new ForeignBankId(1L, JwtPlaceholders.CLIENT_ID.toString()));
        req.setStatus(RequestStatus.ACTIVE);
        req.setPremium(momo);
        req.setPricePerStock(momo);
        req.setSettlementDate(LocalDate.parse("2025-04-11"));
        req.setPricePerStock(momo);
        req.setAmount(1);

        otcRequestRepository.save(req);
        return req;
    }

}
