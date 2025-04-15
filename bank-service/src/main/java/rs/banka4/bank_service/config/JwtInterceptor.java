package rs.banka4.bank_service.config;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class JwtInterceptor implements Interceptor {

    private String getJwtToken() {
        return "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhNGJmMzcwZS0yMTI5LTQxMTYtOTI0My0wYzRlYWQwZmU0M2UiLCJwcml2aWxlZ2VzIjpbIkFETUlOIl0sInR5cCI6IkFDQ0VTUyIsInZlciI6Mywicm9sZSI6IkVNUExPWUVFIn0.E0PwDfFI1vemqjmr6jPDvCN-N288iySu2ao1B90MBIhgpEAVfTP_adnCVH0PfEBqgdxyYfZeP7V9FAtPfVg0eA";
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String jwtToken = getJwtToken();
        if (jwtToken != null && !jwtToken.isEmpty()) {
            Request newRequest =
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + jwtToken)
                    .build();
            return chain.proceed(newRequest);
        }

        return chain.proceed(originalRequest);
    }
}
