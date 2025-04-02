package rs.banka4.user_service.config.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rs.banka4.rafeisen.common.security.UserType;
import rs.banka4.user_service.config.WhiteListConfig;
import rs.banka4.user_service.exceptions.jwt.NoJwtProvided;
import rs.banka4.user_service.repositories.ClientRepository;
import rs.banka4.user_service.repositories.EmployeeRepository;
import rs.banka4.user_service.security.AuthenticatedBankUserAuthentication;
import rs.banka4.user_service.security.AuthenticatedBankUserPrincipal;
import rs.banka4.user_service.service.abstraction.JwtService;


/**
 * Filter that checks if the request has a valid JWT token in the Authorization header.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;

    /**
     * Filters the incoming request to check if it contains a valid JWT token in the Authorization
     * header. If a valid token is found, the method sets the user authentication in the
     * {@link SecurityContextHolder}. If the token is invalid or not provided, an exception is
     * thrown.
     * <p>
     * This method is invoked once per request, ensuring that the request is authenticated before
     * reaching the intended endpoint.
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response that will be sent back to the client
     * @param filterChain the chain of filters to be applied after this filter
     * @throws ServletException if a servlet-related error occurs during the filtering process
     * @throws IOException if an I/O error occurs during request or response handling
     */
    @Override
    public void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException,
        IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        UUID userId = null;
        String token = null;

        if (!WhiteListConfig.isWhitelisted(request.getRequestURI())) {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
                userId = jwtService.extractUserId(token);
            } else {
                throw new NoJwtProvided();
            }
        }

        if (
            userId != null
                && SecurityContextHolder.getContext()
                    .getAuthentication()
                    == null
        ) {
            if (jwtService.validateToken(token)) {
                final var role = jwtService.extractRole(token);

                /*
                 * XXX: temporary kludge in order to populate user privileges.
                 */
                final var userType = UserType.valueOf(role.toUpperCase());
                final var user = switch (userType) {
                /* DO NOT ADD A DEFAULT CASE. */
                case CLIENT -> clientRepository.findById(userId);
                case EMPLOYEE -> employeeRepository.findById(userId);
                };
                if (!user.isPresent()) {
                    throw new NoJwtProvided();
                }

                final var authentication =
                    new AuthenticatedBankUserAuthentication(
                        new AuthenticatedBankUserPrincipal(userType, userId),
                        token,
                        user.get()
                            .getPrivileges()
                    );

                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
