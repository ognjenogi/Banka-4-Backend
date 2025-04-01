package rs.banka4.stock_service;

import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableScheduling
public class StockServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockServiceApplication.class, args);
    }

    @GetMapping("/")
    @Operation(summary = "Get a cute message")
    public Map<String, ?> hello() {
        return Map.of("hello", "world");
    }
}
