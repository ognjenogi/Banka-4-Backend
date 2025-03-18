package rs.banka4.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import rs.banka4.user_service.domain.loan.db.LoanInstallment;
import rs.banka4.user_service.domain.loan.dtos.LoanInstallmentDto;
import rs.banka4.user_service.domain.loan.mapper.LoanMapper;
import rs.banka4.user_service.domain.loan.specification.LoanSpecification;
import rs.banka4.user_service.exceptions.loan.LoanNotFound;
import rs.banka4.user_service.repositories.LoanInstallmentRepository;
import rs.banka4.user_service.repositories.LoanRepository;
import rs.banka4.user_service.service.abstraction.LoanInstallmentService;
@RequiredArgsConstructor
@Service
public class LoanInstallmentServiceImpl implements LoanInstallmentService {
    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    @Override
    public Page<LoanInstallmentDto> getInstallmentsForLoan(Long loanNumber, int page, int size) {
        var loan =
                loanRepository.findByLoanNumber(loanNumber)
                        .orElseThrow(LoanNotFound::new);
        Page<LoanInstallment> pages = loanInstallmentRepository.findAll(LoanSpecification.findPaidAndNextUpcomingInstallment(loan.getId()), PageRequest.of(page,size));

        return pages.map(LoanMapper.INSTANCE::toDto);
    }
}
