package ru.pstu.lamsv2.dto.application.userDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 DTO для передачи пользователю его токенов при успешной авторизации.
    1. accessToken - временный
    2. refreshToken - постоянный в БД
*/

@Getter
@Setter
@AllArgsConstructor
public class AuthResponseDTO
{
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;

    public AuthResponseDTO(
            String accessToken,
            String refreshToken
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
    }
}
