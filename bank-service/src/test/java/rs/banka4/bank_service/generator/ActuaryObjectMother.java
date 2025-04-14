package rs.banka4.bank_service.generator;

import java.math.BigDecimal;
import rs.banka4.bank_service.domain.actuaries.db.ActuaryInfo;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.user.User;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

public class ActuaryObjectMother {

    /**
     * Generates a basic ActuaryInfo instance.
     *
     * @return a new ActuaryInfo instance
     */
    public static ActuaryInfo generateBasicActuaryInfo(User user) {
        return ActuaryInfo.builder()
            .userId(user.getId())
            .needApproval(true)
            .limit(new MonetaryAmount(BigDecimal.valueOf(1_000_000), CurrencyCode.RSD))
            .usedLimit(new MonetaryAmount(BigDecimal.valueOf(1000), CurrencyCode.RSD))
            .build();
    }

}
