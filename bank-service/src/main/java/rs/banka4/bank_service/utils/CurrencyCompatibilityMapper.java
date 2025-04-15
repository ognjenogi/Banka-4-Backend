package rs.banka4.bank_service.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rs.banka4.bank_service.domain.account.dtos.CurrencyDto;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

@Mapper
public interface CurrencyCompatibilityMapper {
    @Mapping(
        target = "code",
        source = "currencyCode"
    )
    CurrencyDto toCurrencyDto(CurrencyCode currencyCode);
}
