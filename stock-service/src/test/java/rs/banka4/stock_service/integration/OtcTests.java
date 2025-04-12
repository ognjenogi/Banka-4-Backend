package rs.banka4.stock_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static rs.banka4.stock_service.generator.OtcRequestGen.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import rs.banka4.stock_service.domain.trading.db.OtcRequest;
import rs.banka4.stock_service.domain.trading.db.RequestStatus;
import rs.banka4.stock_service.domain.trading.db.dtos.OtcRequestUpdateDto;
import rs.banka4.stock_service.repositories.AssetRepository;
import rs.banka4.stock_service.repositories.OtcRequestRepository;
import rs.banka4.stock_service.repositories.SecurityRepository;
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
        OtcRequest dummyRequest =
            createDummyOtcRequest(assetRepository, securityRepository, otcRequestRepository);
        createDummyOtcRequestMeRead(assetRepository, securityRepository, otcRequestRepository);
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
        OtcRequest dummyRequest =
            createDummyOtcRequestNotMe(assetRepository, securityRepository, otcRequestRepository);

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
        OtcRequest dummyRequest =
            createDummyOtcRequestMeRead(assetRepository, securityRepository, otcRequestRepository);
        createDummyOtcRequestMeUnread(assetRepository, securityRepository, otcRequestRepository);
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
        OtcRequest dummyRequest =
            createDummyOtcRequestNotMe(assetRepository, securityRepository, otcRequestRepository);
        createDummyOtcRequestMeRead(assetRepository, securityRepository, otcRequestRepository);

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
        OtcRequest dummyRequest =
            createDummyOtcRequestNotMe(assetRepository, securityRepository, otcRequestRepository);
        var id = dummyRequest.getId();
        createDummyOtcRequestMeRead(assetRepository, securityRepository, otcRequestRepository);

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
        OtcRequest dummyRequest =
            createDummyOtcRequestMeRead(assetRepository, securityRepository, otcRequestRepository);
        var id = dummyRequest.getId();
        var momo = new MonetaryAmount();
        momo.setAmount(BigDecimal.TEN);
        momo.setCurrency(CurrencyCode.CAD);
        var updateDto = new OtcRequestUpdateDto(null, momo, 10, LocalDate.parse("2025-04-11"));
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
        assertEquals(
            dummyRequest.getPricePerStock()
                .getCurrency(),
            otcRejected.getPricePerStock()
                .getCurrency()
        );
        assertEquals(
            momo.getCurrency(),
            otcRejected.getPremium()
                .getCurrency()
        );
        assertEquals(10, otcRejected.getAmount());
        assertEquals(
            JwtPlaceholders.CLIENT_ID.toString(),
            otcRejected.getModifiedBy()
                .userId()
        );
    }
}
