package rs.banka4.bank_service.runners;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import rs.banka4.bank_service.utils.DataSourceService;

@Component
@RequiredArgsConstructor
public class TestDataRunner implements CommandLineRunner {
    private final Environment environment;
    private final DataSourceService dataSourceService;

    @Override
    public void run(String... args) {
        if (environment.acceptsProfiles(Profiles.of("dev"))) {
            dataSourceService.insertData(true);
        }

//        dataSourceService.insertData(environment.matchesProfiles("dev"));
    }
}
