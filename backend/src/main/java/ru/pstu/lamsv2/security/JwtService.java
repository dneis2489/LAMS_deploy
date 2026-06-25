package ru.pstu.lamsv2.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
    Класс для создания и проверки JWT токенов
*/

@Component
public class JwtService
{

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties)
    {
        this.jwtProperties = jwtProperties;
        //Получает настройки JWT токенов из yml файла
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.secret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    //Создает ключ, которым будут подписываться JWT токены
    public String generateAccessToken(CustomUserDetails userDetails)
    {
        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + jwtProperties.accessTokenExpirationMs()
        );

        //Внутри токена будут:
        return Jwts.builder()
                .subject(userDetails.getId().toString()) //id пользователя
                .claim("email", userDetails.getEmail()) //email пользователя
                .claim("username", userDetails.getDisplayUsername()) //username пользователя
                .claim("role", userDetails.getRole()) //роль пользователя
                .issuedAt(now) //Время создания токена
                .expiration(expiration) //Время когда токен станет недействительным
                .signWith(secretKey) //Подписывает токен секретным ключом
                .compact();
    }

    //Получение id пользователя из токена
    public UUID extractUserId(String token)
    {
        return UUID.fromString(
                extractAllClaims(token).getSubject()
        );
    }

    //Получение email пользователя из токена
    public String extractEmail(String token)
    {
        return extractAllClaims(token)
                .get("email", String.class);
    }

    //Получение роли пользователя из токена
    public String extractRole(String token)
    {
        return extractAllClaims(token)
                .get("role", String.class);
    }

    //Проверка валидности токена. Берем id пользователя из базы и из токена и проверям, что они одинаковые.
    //Проверяет, что токен активный
    public boolean isTokenValid(
            String token,
            CustomUserDetails userDetails
    )
    {
        UUID userId = extractUserId(token);

        return userId.equals(userDetails.getId())
                && !isTokenExpired(token);
    }

    //Проверяет, вышло ли время работы токена. Если токен просрочен, то использовать нельзя.
    private boolean isTokenExpired(String token)
    {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    //Разбивает токен на составляющие
    private Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
