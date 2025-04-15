package rs.banka4.bank_service.utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import rs.banka4.bank_service.domain.actuaries.db.ActuaryInfo;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.integration.generator.UserGenerator;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

/**
 * Test-writing utility class for generating various actuary instances.
 */
public class ActuaryGenerator {
    public static final UUID ACTUARY_1_UUID =
        UUID.fromString("6d03b02b-b2d7-4de6-b2d5-9917f44d2f5a");
    public static final UUID ACTUARY_2_UUID =
        UUID.fromString("a74c8e17-31f8-4f47-bb99-63c77d3b8d0e");
    public static final UUID FOR_NEWLY_CREATED_ACTUARY_3_UUID = UUID.randomUUID();

    public static List<ActuaryInfo> makeExampleActuaries(UserGenerator userGen) {
        userGen.createEmployee(
            x -> x.id(ACTUARY_1_UUID)
                .email("ddx1@gmail.com")
                .username("saban1")
        );
        userGen.createEmployee(
            x -> x.id(ACTUARY_2_UUID)
                .email("ddx2@gmail.com")
                .username("saban2")
        );
        return List.of(
            ActuaryInfo.builder()
                .userId(ACTUARY_1_UUID)
                .needApproval(true)
                .limit(new MonetaryAmount(new BigDecimal("10000"), CurrencyCode.RSD))
                .usedLimit(new MonetaryAmount(new BigDecimal("2500"), CurrencyCode.RSD))
                .build(),

            ActuaryInfo.builder()
                .userId(ACTUARY_2_UUID)
                .needApproval(false)
                .limit(new MonetaryAmount(new BigDecimal("50000"), CurrencyCode.USD))
                .usedLimit(new MonetaryAmount(new BigDecimal("10000"), CurrencyCode.USD))
                .build()
        );
    }
}
