package ru.pstu.lamsv2.dto.getDataInDB.userDTO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 DTO для получения данных о пользователе системы
*/

@Getter
@Setter
@AllArgsConstructor
public class UserDTO
{
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
