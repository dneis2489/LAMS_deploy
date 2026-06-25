package ru.pstu.lamsv2.dto.application.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 DTO для данных авторизации пользователя.
*/

@Getter
@Setter
@AllArgsConstructor
public class LoginDTO
{
    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    private String password;
}
