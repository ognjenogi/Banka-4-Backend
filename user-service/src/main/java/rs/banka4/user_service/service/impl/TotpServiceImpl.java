package rs.banka4.user_service.service.impl;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import rs.banka4.user_service.domain.authenticator.db.UserTotpSecret;
import rs.banka4.user_service.domain.authenticator.dtos.RegenerateAuthenticatorResponseDto;
import rs.banka4.user_service.domain.user.client.db.Client;
import rs.banka4.user_service.domain.user.employee.db.Employee;
import rs.banka4.user_service.exceptions.authenticator.NoTotpException;
import rs.banka4.user_service.exceptions.authenticator.NotActiveTotpException;
import rs.banka4.user_service.exceptions.authenticator.NotValidTotpException;
import rs.banka4.user_service.exceptions.user.NotAuthenticated;
import rs.banka4.user_service.exceptions.user.NotFound;
import rs.banka4.user_service.repositories.ClientRepository;
import rs.banka4.user_service.repositories.EmployeeRepository;
import rs.banka4.user_service.repositories.UserTotpSecretRepository;
import rs.banka4.user_service.service.abstraction.JwtService;
import rs.banka4.user_service.service.abstraction.TotpService;

@Service
@RequiredArgsConstructor
public class TotpServiceImpl implements TotpService {

    private static final int SECRET_LENGTH = 32;
    private static final int TIME_PERIOD = 30;
    private static final int CODE_LENGTH = 6;
    private static final HashingAlgorithm ALGORITHM = HashingAlgorithm.SHA1;

    private final JwtService jwtService;
    private final UserTotpSecretRepository repository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public void verifyNewAuthenticator(Authentication auth, String code) {
        String email = getEmail(extractToken(auth));
        UserTotpSecret totp = getTotpSecretByEmail(email);
        validateTotpCode(totp, code);
        totp.setIsActive(true);
        repository.save(totp);
    }

    @Override
    public boolean validate(String authorization, String code) {
        String email = getEmail(authorization);
        UserTotpSecret totp = getTotpSecretByEmail(email);
        if (!totp.getIsActive()) {
            throw new NotActiveTotpException();
        }
        return isCodeValid(totp.getSecret(), code);
    }

    @Override
    public RegenerateAuthenticatorResponseDto regenerateSecret(Authentication auth) {
        String token = extractToken(auth);
        UUID userId = jwtService.extractUserId(token);

        // TODO: ?
        if (jwtService.isTokenExpired(token)) throw new NotAuthenticated();
        if (jwtService.isTokenInvalidated(token)) throw new NotAuthenticated();

        SecretGenerator secretGenerator = new DefaultSecretGenerator(SECRET_LENGTH);
        String newSecret = secretGenerator.generate();

        UserTotpSecret userTotpSecret;
        Optional<Client> client = clientRepository.findById(userId);
        Optional<Employee> employee = employeeRepository.findById(userId);
        String currentEmail = "";

        if (client.isPresent()) {
            Client safeClient = client.get();
            userTotpSecret =
                repository.findByClient_Email(
                    client.get()
                        .getEmail()
                )
                    .map(secretObj -> {
                        secretObj.setSecret(newSecret);
                        secretObj.setIsActive(false);
                        return secretObj;
                    })
                    .orElseGet(() -> new UserTotpSecret(null, newSecret, safeClient, null, false));
            currentEmail = safeClient.getEmail();
        } else if (employee.isPresent()) {
            Employee safeEmployee = employee.get();
            userTotpSecret =
                repository.findByEmployee_Email(
                    employee.get()
                        .getEmail()
                )
                    .map(secretObj -> {
                        secretObj.setSecret(newSecret);
                        secretObj.setIsActive(false);
                        return secretObj;
                    })
                    .orElseGet(
                        () -> new UserTotpSecret(null, newSecret, null, safeEmployee, false)
                    );
            currentEmail = safeEmployee.getEmail();
        } else {
            throw new NotFound();
        }

        repository.save(userTotpSecret);

        return new RegenerateAuthenticatorResponseDto(
            createTotpUrl("RAFeisen", currentEmail, newSecret),
            newSecret
        );
    }

    @Override
    public boolean verifyClient(Authentication authentication, String otpCode) {
        return validate(
            authentication.getCredentials()
                .toString(),
            otpCode
        );
    }

    @Override
    public String generateCode(String authorization) {
        String email = getEmail(authorization);
        if (jwtService.isTokenExpired(authorization)) throw new NotAuthenticated();
        if (jwtService.isTokenInvalidated(authorization)) throw new NotAuthenticated();

        UserTotpSecret totp = getTotpSecretByEmail(email);
        return generateCodeFromSecret(totp.getSecret());
    }


    // --- Private helper methods ---

    private String getEmail(String authorization) {
        String role = jwtService.extractRole(authorization);
        UUID userId = jwtService.extractUserId(authorization);

        if ("client".equalsIgnoreCase(role)) {
            Optional<Client> client = clientRepository.findById(userId);
            return client.get()
                .getEmail();
        } else {
            Optional<Employee> employee = employeeRepository.findById(userId);
            return employee.get()
                .getEmail();
        }
    }

    private String generateCodeFromSecret(String secret) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator(ALGORITHM);
        long currentBucket = timeProvider.getTime() / TIME_PERIOD;
        try {
            return codeGenerator.generate(secret, currentBucket);
        } catch (CodeGenerationException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isCodeValid(String secret, String code) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator(ALGORITHM);
        DefaultCodeVerifier validator = new DefaultCodeVerifier(codeGenerator, timeProvider);
        return validator.isValidCode(secret, code);
    }

    private void validateTotpCode(UserTotpSecret totp, String code) {
        if (!isCodeValid(totp.getSecret(), code)) {
            throw new NotValidTotpException();
        }
    }

    private UserTotpSecret getTotpSecretByEmail(String email) {
        return repository.findByClient_Email(email)
            .or(() -> repository.findByEmployee_Email(email))
            .orElseThrow(NoTotpException::new);
    }

    private String extractToken(Authentication auth) {
        return auth.getCredentials()
            .toString()
            .replace("Bearer ", "");
    }

    private String createTotpUrl(String issuer, String email, String secret) {
        return String.format(
            "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=%s&digits=%d&period=%d",
            issuer,
            email,
            secret,
            issuer,
            ALGORITHM,
            CODE_LENGTH,
            TIME_PERIOD
        );
    }
}
