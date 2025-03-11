package rs.banka4.user_service.unit.loan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rs.banka4.user_service.domain.loan.db.Loan;
import rs.banka4.user_service.domain.loan.db.LoanStatus;
import rs.banka4.user_service.exceptions.loan.LoanNotFound;
import rs.banka4.user_service.repositories.LoanRepository;
import rs.banka4.user_service.service.impl.LoanServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ManageLoansTests {
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    void approveLoan_success() {
        Long loanNumber = 123L;
        Loan loan = new Loan();

        loan.setLoanNumber(loanNumber);
        loan.setStatus(LoanStatus.PROCESSING);

        when(loanRepository.findByLoanNumber(loanNumber))
                .thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);

        loanService.approveLoan(loanNumber);

        assertEquals(LoanStatus.APPROVED, loan.getStatus());
        verify(loanRepository).save(loan);
    }

    @Test
    void approveLoan_notFound() {
        Long loanNumber = 123L;
        when(loanRepository.findByLoanNumber(loanNumber))
                .thenReturn(Optional.empty());

        assertThrows(LoanNotFound.class, () -> loanService.approveLoan(loanNumber));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void rejectLoan_success() {
        Long loanNumber = 456L;
        Loan loan = new Loan();
        loan.setLoanNumber(loanNumber);
        loan.setStatus(LoanStatus.PROCESSING);

        when(loanRepository.findByLoanNumber(loanNumber))
                .thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);

        loanService.rejectLoan(loanNumber);

        assertEquals(LoanStatus.REJECTED, loan.getStatus());
        verify(loanRepository).save(loan);
    }

    @Test
    void rejectLoan_notFound() {
        Long loanNumber = 456L;
        when(loanRepository.findByLoanNumber(loanNumber))
                .thenReturn(Optional.empty());

        assertThrows(LoanNotFound.class, () -> loanService.rejectLoan(loanNumber));
        verify(loanRepository, never()).save(any(Loan.class));
    }
}
