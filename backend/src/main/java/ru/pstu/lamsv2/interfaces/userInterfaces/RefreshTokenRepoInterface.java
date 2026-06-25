package ru.pstu.lamsv2.interfaces.userInterfaces;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
    Интерфейс описывающий методы репозитория работы с RefreshToken. Содержит методы:
        1. Создание RefreshToken
        2. Проверка, какому пользователю принадлежит токен
        3. Проверка валидность токена
        4. Удаление токена для выхода из системы
        5. Удаление всех токенов у конкретного пользователя. Для деактивации всех аккаунтов
*/

public interface RefreshTokenRepoInterface
{
    //Создает RefreshToken
    void create(
            UUID userId,
            String tokenHash,
            LocalDateTime expiresAt
    );

    //Проверяем, какому пользователю принадлежит токен
    Optional<UUID> findUserIdByValidTokenHash(String tokenHash);

    //Проверяет валидность токена
    boolean existsValidToken(
            UUID userId,
            String tokenHash
    );

    //Удаляет токен для выхода из системы
    void deleteByTokenHash(String tokenHash);

    //Удаляет все токены у конкретного пользователя
    void deleteAllByUserId(UUID userId);
}
