package rs.banka4.user_service.domain.user;

import org.springframework.security.core.GrantedAuthority;

public enum Privilege implements GrantedAuthority {
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }

    public long bit() {
        return 1L << this.ordinal();
    }
}
