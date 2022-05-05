package com.sobkolex.account_service.entity;

import com.sobkolex.account_service.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotEmpty
    private String name;

    @Column
    @NotEmpty
    private String lastname;

    @Column
    @NotEmpty
    @Pattern(regexp = "\\w+@acme.com", message = "Wrong email format!")
    private String email;

    @Column
    @NotEmpty
    private String password;

    @Column
    @Enumerated(value = EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = Role.class)
    private List<Role> roles;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @Column(name = "failed_attempt")
    private int failedAttempt;

    public void addRole(Role role) {
        this.roles.add(0, role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()   {
        return getRoles().stream()
                .flatMap(role -> role.getAuthorities().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
