package rs.banka4.bank_service.domain.loan.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import rs.banka4.bank_service.domain.account.mapper.AccountMapper;
import rs.banka4.bank_service.domain.loan.db.Loan;
import rs.banka4.bank_service.domain.loan.db.LoanInstallment;
import rs.banka4.bank_service.domain.loan.db.LoanRequest;
import rs.banka4.bank_service.domain.loan.dtos.*;
import rs.banka4.bank_service.domain.loan.dtos.LoanApplicationDto;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {
        AccountMapper.class
    }
)
public interface LoanMapper {

    LoanMapper INSTANCE = Mappers.getMapper(LoanMapper.class);

    LoanInstallmentDto toDto(LoanInstallment loanInstallment);

    @Mapping(
        target = "currency",
        expression = "java(new CurrencyDto(loan.getAccount().getCurrency()))"
    )
    LoanInformationDto toDto(Loan loan);

    @Mapping(
        source = "loan.loanNumber",
        target = "loanNumber"
    )
    @Mapping(
        target = "loanType",
        source = "type"
    )
    @Mapping(
        target = "accountNumber",
        expression = "java(loanRequest.getAccount().getAccountNumber())"
    )
    @Mapping(
        target = "currency",
        expression = "java(new CurrencyDto(loanRequest.getAccount().getCurrency()))"
    )
    LoanApplicationResponseDto toDtoApplicationResponse(LoanRequest loanRequest);

    @Mapping(
        source = "loanType",
        target = "type"
    )
    Loan toEntity(LoanApplicationDto loanApplicationDto);

    @Mapping(
        target = "currency",
        ignore = true
    )
    @Mapping(
        target = "type",
        source = "loanType"
    )
    LoanRequest toLoanRequest(LoanApplicationDto loanApplicationDto);


}
