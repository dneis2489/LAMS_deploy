package ru.pstu.lamsv2.dto.application.userDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 DTO для передачи RefreshToken в системе
*/

@Getter
@Setter
@AllArgsConstructor
public class RefreshTokenDTO
{
    @NotBlank(message = "Refresh token обязателен")
    private String refreshToken;
}
