package rs.banka4.user_service.utils;

import org.springframework.security.core.GrantedAuthority;
import rs.banka4.rafeisen.common.security.Privilege;

public class SecurityUtils {
    public static GrantedAuthority asGrantedAuthority(Privilege privilege) {
        return privilege::name;
    }
}
