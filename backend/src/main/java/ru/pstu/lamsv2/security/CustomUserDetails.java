package ru.pstu.lamsv2.security;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

/**
    Объект для хранения данных о пользователе. Имплементирует UserDetails из Spring Security
*/

public class CustomUserDetails implements UserDetails {

    private final UUID id;
    private final String email;
    private final String username;
    private final String password;
    private final String role;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(
            UUID id,
            String email,
            String username,
            String password,
            String role,
            boolean enabled,
            Collection<? extends GrantedAuthority> authorities
    )
    {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public UUID getId() {return id;}

    public String getEmail() {return email;}

    public String getDisplayUsername() {return username;}

    public String getRole() {return role;}

    @Override
    public String getUsername() {return email;}

    @Override
    public String getPassword() {return password;}

    @Override
    public boolean isEnabled() {return enabled;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {return authorities;}
}
