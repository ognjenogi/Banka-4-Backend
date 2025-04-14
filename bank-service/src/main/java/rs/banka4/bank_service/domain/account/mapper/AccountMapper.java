package rs.banka4.bank_service.domain.account.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import rs.banka4.bank_service.domain.account.db.Account;
import rs.banka4.bank_service.domain.account.dtos.AccountDto;
import rs.banka4.bank_service.domain.account.dtos.AccountTypeDto;
import rs.banka4.bank_service.domain.account.dtos.CreateAccountDto;
import rs.banka4.bank_service.domain.company.mapper.CompanyMapper;
import rs.banka4.bank_service.domain.user.client.mapper.ClientMapper;
import rs.banka4.bank_service.domain.user.employee.mapper.EmployeeMapper;
import rs.banka4.rafeisen.common.currency.CurrencyCode;
import rs.banka4.rafeisen.common.dto.AccountNumberDto;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {
        CompanyMapper.class,ClientMapper.class,EmployeeMapper.class
    }
)
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    @Mapping(
        target = "accountType",
        expression = "java(toAccountTypeDto(account))"
    )
    AccountDto toDto(Account account);

    Account toEntity(CreateAccountDto dto);

    @Named("toAccountTypeDto")
    default AccountTypeDto toAccountTypeDto(Account account) {
        if (account == null || account.getCurrency() == null || account.getAccountType() == null) {
            return null;
        }
        if (account.getCurrency() == CurrencyCode.RSD) {
            if (
                account.getAccountType()
                    .isBusiness()
            ) {
                return AccountTypeDto.CheckingBusiness;
            } else {
                return AccountTypeDto.CheckingPersonal;
            }
        } else {
            if (
                account.getAccountType()
                    .isBusiness()
            ) {
                return AccountTypeDto.FxBusiness;
            } else {
                return AccountTypeDto.FxPersonal;
            }
        }
    }

    @Mapping(
        target = "currency",
        expression = "java(account.getCurrency())"
    )
    AccountNumberDto toAccountNumberDto(Account account);
}
