package ru.pstu.lamsv2.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
    Конфиг создает passwordEncoder для паролей пользователей. Так как все пароли в БД зашифрованы
*/
@Configuration
public class PasswordConfig
{
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
