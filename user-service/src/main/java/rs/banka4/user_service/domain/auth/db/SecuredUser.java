package rs.banka4.user_service.domain.auth.db;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rs.banka4.user_service.domain.user.User;
import rs.banka4.user_service.domain.user.employee.db.Employee;

public class SecuredUser implements UserDetails {

    private final User user;

    public SecuredUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities =
            user.getPrivileges()
                .stream()
                .map(privilege -> (GrantedAuthority) privilege::name)
                .collect(Collectors.toSet());

        if (user instanceof Employee) {
            authorities.add((GrantedAuthority) () -> "EMPLOYEE");
        } else {
            authorities.add((GrantedAuthority) () -> "CLIENT");
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
