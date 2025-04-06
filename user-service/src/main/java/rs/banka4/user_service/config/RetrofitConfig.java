package rs.banka4.user_service.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

@Configuration
public class RetrofitConfig {

    @Bean
    public Retrofit stockServiceRetrofit() {
        return createRetrofit("http://gateway:80/stock/");
    }

    private Retrofit createRetrofit(String baseUrl) {
        OkHttpClient client = new OkHttpClient.Builder()
            .hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    // Ignore hostname verification
                    return true;
                }
            })
            .build();

        return new Retrofit.Builder().baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    }
}
