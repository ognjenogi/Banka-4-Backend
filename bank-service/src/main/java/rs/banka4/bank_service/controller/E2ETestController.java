package rs.banka4.bank_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.banka4.bank_service.utils.DataSourceService;

/** Handles routes used by E2E tests. !!! RISKY !!! */
@RestController
@RequestMapping("/e2e")
@RequiredArgsConstructor
@Profile("e2e")
@Slf4j
public class E2ETestController {
    private final DataSourceService dataSource;

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/redo-data")
    public void redoData() {
        /* A little trolling. */
        log.info("yeetus deletus the datatus");

        jdbcTemplate.execute("""
            TRUNCATE TABLE accounts RESTART IDENTITY CASCADE;
            TRUNCATE TABLE activity_codes RESTART IDENTITY CASCADE;
            TRUNCATE TABLE actuary_informations RESTART IDENTITY CASCADE;
            TRUNCATE TABLE asset_ownership RESTART IDENTITY CASCADE;
            TRUNCATE TABLE assets RESTART IDENTITY CASCADE;
            TRUNCATE TABLE bank_margins RESTART IDENTITY CASCADE;
            TRUNCATE TABLE cards RESTART IDENTITY CASCADE;
            TRUNCATE TABLE client_contacts RESTART IDENTITY CASCADE;
            TRUNCATE TABLE clients RESTART IDENTITY CASCADE;
            TRUNCATE TABLE companies RESTART IDENTITY CASCADE;
            TRUNCATE TABLE employees RESTART IDENTITY CASCADE;
            TRUNCATE TABLE exchanges RESTART IDENTITY CASCADE;
            TRUNCATE TABLE forex_pairs RESTART IDENTITY CASCADE;
            TRUNCATE TABLE futures RESTART IDENTITY CASCADE;
            TRUNCATE TABLE interest_rates RESTART IDENTITY CASCADE;
            TRUNCATE TABLE listing_daily_price_info RESTART IDENTITY CASCADE;
            TRUNCATE TABLE listings RESTART IDENTITY CASCADE;
            TRUNCATE TABLE loan_installments RESTART IDENTITY CASCADE;
            TRUNCATE TABLE loan_requests RESTART IDENTITY CASCADE;
            TRUNCATE TABLE loans RESTART IDENTITY CASCADE;
            TRUNCATE TABLE options RESTART IDENTITY CASCADE;
            TRUNCATE TABLE orders RESTART IDENTITY CASCADE;
            TRUNCATE TABLE otc_requests RESTART IDENTITY CASCADE;
            TRUNCATE TABLE securities RESTART IDENTITY CASCADE;
            TRUNCATE TABLE stocks RESTART IDENTITY CASCADE;
            TRUNCATE TABLE tokens RESTART IDENTITY CASCADE;
            TRUNCATE TABLE transactions RESTART IDENTITY CASCADE;
            TRUNCATE TABLE user_tax_debts RESTART IDENTITY CASCADE;
            TRUNCATE TABLE user_to_totp_secrets RESTART IDENTITY CASCADE;
            TRUNCATE TABLE users RESTART IDENTITY CASCADE;
            TRUNCATE TABLE verification_tokens RESTART IDENTITY CASCADE;
            """);

        dataSource.insertData(true);
    }
}
