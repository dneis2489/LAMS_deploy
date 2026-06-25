package ru.pstu.lamsv2.dto.application.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/** DTO для безопасного обновления пользователя через JSON body. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO
{
    @NotNull(message = "Id обязателен")
    private UUID id;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    private String email;

    @NotBlank(message = "Username обязателен")
    @Size(min = 3, max = 50)
    private String username;

    @NotNull(message = "RoleId обязателен")
    private Long roleId;

    @Pattern(
            regexp = "^$|^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,100}$",
            message = "Пароль должен содержать минимум 8 символов, заглавную букву, строчную букву, цифру и спецсимвол"
    )
    private String password;

    private boolean enabled;
}
