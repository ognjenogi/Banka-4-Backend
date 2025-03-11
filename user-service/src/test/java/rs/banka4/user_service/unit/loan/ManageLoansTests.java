package rs.banka4.user_service.unit.loan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rs.banka4.user_service.domain.loan.db.Loan;
import rs.banka4.user_service.domain.loan.db.LoanStatus;
import rs.banka4.user_service.exceptions.Unauthorized;
import rs.banka4.user_service.exceptions.loan.LoanNotFound;
import rs.banka4.user_service.repositories.LoanRepository;
import rs.banka4.user_service.service.impl.LoanServiceImpl;
import rs.banka4.user_service.utils.JwtUtil;

import java.time.LocalDate;
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
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    void unauthorizedJwt_approveLoan(){
        when(jwtUtil.extractRole("jwt")).thenReturn("client");

        assertThrows(Unauthorized.class, () -> loanService.approveLoan(123L,"jwt"));
        verify(loanRepository, never()).save(any(Loan.class));
    }
    @Test
    void unauthorizedJwt_rejectLoan(){
        when(jwtUtil.extractRole("jwt")).thenReturn("client");

        assertThrows(Unauthorized.class, () -> loanService.rejectLoan(123L,"jwt"));
        verify(loanRepository, never()).save(any(Loan.class));
    }
    @Test
    void approveLoan_success() {
        Long loanNumber = 123L;
        Loan loan = new Loan();

        loan.setLoanNumber(loanNumber);
        loan.setStatus(LoanStatus.PROCESSING);
        when(jwtUtil.extractRole("jwt")).thenReturn("employee");

        when(loanRepository.findByLoanNumber(loanNumber))
                .thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);

        loanService.approveLoan(loanNumber,"jwt");

        assertEquals(loan.getAgreementDate(), LocalDate.now());
        assertEquals(LoanStatus.APPROVED, loan.getStatus());
        verify(loanRepository).save(loan);
    }

    @Test
    void approveLoan_notFound() {
        Long loanNumber = 123L;
        when(loanRepository.findByLoanNumber(loanNumber))
                .thenReturn(Optional.empty());
        when(jwtUtil.extractRole("jwt")).thenReturn("employee");


        assertThrows(LoanNotFound.class, () -> loanService.approveLoan(loanNumber,"jwt"));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void rejectLoan_success() {
        Long loanNumber = 456L;
        Loan loan = new Loan();
        loan.setLoanNumber(loanNumber);
        loan.setStatus(LoanStatus.PROCESSING);

        when(jwtUtil.extractRole("jwt")).thenReturn("employee");
        when(loanRepository.findByLoanNumber(loanNumber))
                .thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);

        loanService.rejectLoan(loanNumber, "jwt");

        assertEquals(LoanStatus.REJECTED, loan.getStatus());
        verify(loanRepository).save(loan);
    }

    @Test
    void rejectLoan_notFound() {
        Long loanNumber = 456L;

        when(jwtUtil.extractRole("jwt")).thenReturn("employee");
        when(loanRepository.findByLoanNumber(loanNumber))
                .thenReturn(Optional.empty());

        assertThrows(LoanNotFound.class, () -> loanService.rejectLoan(loanNumber,"jwt"));
        verify(loanRepository, never()).save(any(Loan.class));
    }
}
