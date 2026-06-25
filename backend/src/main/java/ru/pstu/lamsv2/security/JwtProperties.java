package ru.pstu.lamsv2.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
     Объект для хранения настроек jwt-токенов из yml файла
*/

//Читает настройки jwt из yml файла
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpirationMs,
        long refreshTokenExpirationMs
)
{

}
