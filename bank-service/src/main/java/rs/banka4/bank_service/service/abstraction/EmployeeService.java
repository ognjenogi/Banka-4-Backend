package rs.banka4.bank_service.service.abstraction;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import rs.banka4.bank_service.domain.auth.dtos.LoginDto;
import rs.banka4.bank_service.domain.auth.dtos.LoginResponseDto;
import rs.banka4.bank_service.domain.user.PrivilegesDto;
import rs.banka4.bank_service.domain.user.employee.db.Employee;
import rs.banka4.bank_service.domain.user.employee.dtos.CreateEmployeeDto;
import rs.banka4.bank_service.domain.user.employee.dtos.EmployeeDto;
import rs.banka4.bank_service.domain.user.employee.dtos.UpdateEmployeeDto;
import rs.banka4.rafeisen.common.dto.EmployeeResponseDto;

public interface EmployeeService {
    LoginResponseDto login(LoginDto loginDto);

    EmployeeResponseDto getMe(String authorization);

    ResponseEntity<PrivilegesDto> getPrivileges();

    void createEmployee(CreateEmployeeDto dto);

    ResponseEntity<Page<EmployeeDto>> getAll(
        String firstName,
        String lastName,
        String email,
        String position,
        PageRequest pageRequest
    );

    void activateEmployeeAccount(Employee employee, String password);

    Optional<Employee> findEmployeeByEmail(String email);

    Optional<Employee> findEmployeeById(UUID id);

    void updateEmployee(UUID id, UpdateEmployeeDto updateEmployeeDto);

    EmployeeResponseDto getEmployeeById(UUID id);

    Page<Employee> getAllActuaries(
        String firstName,
        String lastName,
        String email,
        String position,
        PageRequest of
    );
}
