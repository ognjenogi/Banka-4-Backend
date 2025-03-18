package rs.banka4.user_service.unit.loan;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import rs.banka4.user_service.domain.loan.db.Loan;
import rs.banka4.user_service.domain.loan.db.LoanInstallment;
import rs.banka4.user_service.domain.loan.db.PaymentStatus;
import rs.banka4.user_service.domain.loan.dtos.LoanInstallmentDto;
import rs.banka4.user_service.exceptions.loan.LoanNotFound;
import rs.banka4.user_service.repositories.LoanInstallmentRepository;
import rs.banka4.user_service.repositories.LoanRepository;
import rs.banka4.user_service.service.impl.LoanInstallmentServiceImpl;

public class GetAllInstallmentsTests {
    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @InjectMocks
    private LoanInstallmentServiceImpl loanService;

    private Loan testLoan;
    private final Long testLoanNumber = 123L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testLoan = new Loan();
        testLoan.setId(UUID.randomUUID());
        testLoan.setLoanNumber(testLoanNumber);
    }

    @Test
    void testGetInstallmentsForLoan_UpcomingInstallmentExists() {

        when(loanRepository.findByLoanNumber(eq(testLoanNumber))).thenReturn(Optional.of(testLoan));

        LoanInstallment upcoming = new LoanInstallment();
        upcoming.setId(UUID.randomUUID());
        upcoming.setExpectedDueDate(LocalDate.of(2025, 11, 18));
        upcoming.setPaymentStatus(PaymentStatus.UNPAID);

        when(loanInstallmentRepository.findAll(any(Specification.class), any(PageRequest.class)))
            .thenReturn(
                new PageImpl<>(
                    List.of(upcoming),
                    PageRequest.of(
                        0,
                        10,
                        Sort.by("expectedDueDate")
                            .ascending()
                    ),
                    1
                )
            );

        when(loanInstallmentRepository.findAll(any(Specification.class), any(PageRequest.class)))
            .thenReturn(
                new PageImpl<>(
                    List.of(upcoming),
                    PageRequest.of(
                        0,
                        10,
                        Sort.by("expectedDueDate")
                            .ascending()
                    ),
                    1
                )
            );

        Page<LoanInstallmentDto> result = loanService.getInstallmentsForLoan(testLoanNumber, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(loanRepository).findByLoanNumber(eq(testLoanNumber));
        verify(loanInstallmentRepository).findAll(any(Specification.class), any(PageRequest.class));
    }

    @Test
    void testGetInstallmentsForLoan_NoUpcomingInstallment() {
        when(loanRepository.findByLoanNumber(eq(testLoanNumber))).thenReturn(Optional.of(testLoan));
        Page<LoanInstallment> emptyPage =
            new PageImpl<>(
                List.of(),
                PageRequest.of(
                    0,
                    10,
                    Sort.by("expectedDueDate")
                        .ascending()
                ),
                0
            );
        when(loanInstallmentRepository.findAll(any(Specification.class), any(PageRequest.class)))
            .thenReturn(emptyPage);

        Page<LoanInstallmentDto> result = loanService.getInstallmentsForLoan(testLoanNumber, 0, 10);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());

        verify(loanRepository).findByLoanNumber(eq(testLoanNumber));
        verify(loanInstallmentRepository).findAll(any(Specification.class), any(PageRequest.class));
    }

    @Test
    void testGetInstallmentsForLoan_LoanNotFound() {
        when(loanRepository.findByLoanNumber(eq(testLoanNumber))).thenReturn(Optional.empty());

        assertThrows(
            LoanNotFound.class,
            () -> loanService.getInstallmentsForLoan(testLoanNumber, 0, 10)
        );

        verify(loanRepository).findByLoanNumber(eq(testLoanNumber));
    }
}
