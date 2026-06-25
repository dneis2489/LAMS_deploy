package ru.pstu.lamsv2.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.pstu.lamsv2.security.CustomUserDetailsService;

/**
    Проверка логина и пароля для пользователя при авторизации пользователя
*/
@Configuration
public class AuthProviderConfig
{

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthProviderConfig(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    )
    {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        //Поиск пользователя для проверки через CustomUserDetailsService
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);
        //Проверка пароля
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }
}
