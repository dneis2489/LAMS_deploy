package ru.pstu.lamsv2.dto.application.userDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 DTO для добавления нового пользователя в систему
*/

@Getter
@Setter
@AllArgsConstructor
public class CreateUserDTO
{
    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    private String email;

    @NotBlank(message = "Username обязателен")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Pattern(
            regexp =
                    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,100}$",
            message =
                    "Пароль должен содержать минимум 8 символов, заглавную букву, строчную букву, цифру и спецсимвол"
    )
    private String password;

    @NotBlank(message = "Повтор пароля обязателен")
    private String confirmPassword;

    @NotNull(message = "RoleId обязателен")
    private Long roleId;
}
