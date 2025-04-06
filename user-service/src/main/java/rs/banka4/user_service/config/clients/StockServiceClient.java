package rs.banka4.user_service.config.clients;


import org.springframework.http.ResponseEntity;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;
import rs.banka4.user_service.domain.user.employee.dtos.ActuaryPayloadDto;

import java.util.UUID;

public interface StockServiceClient {
    @POST("actuaries/register")
    Call<Void> registerActuary(
        @Header("Authorization") String authorization,
        @Body ActuaryPayloadDto actuaryPayloadDto
    );

    @PUT("actuaries/update")
    Call<Void> updateActuary(
        @Header("Authorization") String authorization,
        @Query("id") UUID id,
        @Body ActuaryPayloadDto actuaryPayloadDto
    );
}
