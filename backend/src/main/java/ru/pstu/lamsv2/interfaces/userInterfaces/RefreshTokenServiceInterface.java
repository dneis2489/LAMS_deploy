package ru.pstu.lamsv2.interfaces.userInterfaces;

import ru.pstu.lamsv2.dto.application.userDTO.AuthResponseDTO;

import java.util.UUID;

/**
    Интерфейс описывающий методы сервиса работы с RefreshToken. Содержит методы:
        1. Создание RefreshToken
        2. Проверка, какому пользователю принадлежит токен
        3. Проверка валидность токена
        4. Удаление токена для выхода из системы
*/

public interface RefreshTokenServiceInterface
{
    //Создание RefreshToken
    String createRefreshToken(UUID userId);

    //Проверка, какому пользователю принадлежит токен
    String hashToken(String token);

    //Проверка валидность токена
    AuthResponseDTO refreshTokens(String refreshToken);

    //Удаление токена для выхода из системы
    void logout(String refreshToken);
}
