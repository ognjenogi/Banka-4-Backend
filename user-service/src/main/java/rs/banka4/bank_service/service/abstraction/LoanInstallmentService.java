package rs.banka4.bank_service.service.abstraction;

import org.springframework.data.domain.Page;
import rs.banka4.bank_service.domain.loan.dtos.LoanInstallmentDto;

public interface LoanInstallmentService {
    Page<LoanInstallmentDto> getInstallmentsForLoan(
        Long loanNumber,
        int page,
        int size,
        String auth
    );
}
