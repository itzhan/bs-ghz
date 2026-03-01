package com.campus.dining.security;

import com.campus.dining.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
public class LoginUser implements UserDetails {

    private final User user;

    public LoginUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = switch (user.getRole()) {
            case 0 -> "ROLE_USER";
            case 1 -> "ROLE_MERCHANT";
            case 2 -> "ROLE_ADMIN";
            default -> "ROLE_USER";
        };
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == 1;
    }
}
