package rs.banka4.bank_service.security;

import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class PreAuthBankUserAuthentication extends AbstractAuthenticationToken {
    private final UnauthenticatedBankUserPrincipal principal;
    private final String password;

    public PreAuthBankUserAuthentication(
        UnauthenticatedBankUserPrincipal principal,
        String password
    ) {
        super(List.of());
        super.setAuthenticated(false);
        this.principal = principal;
        this.password = password;
    }

    @Override
    public UnauthenticatedBankUserPrincipal getPrincipal() {
        return principal;
    }

    @Override
    public String getCredentials() {
        return password;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated)
            throw new IllegalArgumentException(
                "PreAuthBankUserAuthentication cannot be considered authenicated"
            );
        super.setAuthenticated(false);
    }
}
