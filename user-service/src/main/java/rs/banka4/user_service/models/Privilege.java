package rs.banka4.user_service.models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

@Entity(name = "privilege")
public class Privilege implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}