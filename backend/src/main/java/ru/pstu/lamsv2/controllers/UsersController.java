package ru.pstu.lamsv2.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pstu.lamsv2.dto.application.userDTO.*;
import ru.pstu.lamsv2.dto.getDataInDB.userDTO.*;
import ru.pstu.lamsv2.services.userServices.UsersService;

import java.util.List;
import java.util.UUID;

/**
    Контроллер реализующий методы для работы с пользователями. Данный контроллер реализует следующие эндпоинты:
        1. Авторизации
        2. Обновления токена
        3. Добавления пользователя
        4. Поиск пользователя по email
        5. Поиск пользователя по id
        6. Получение данных о всех пользователях
        7. Обновление данных конкретного пользователя
        8. Удаление пользователя по id
        9. Получение всех пользовательских ролей системы
        10. Выход из системы
*/

@RestController
@RequestMapping("lams")
public class UsersController
{
    private final UsersService usersService;

    public UsersController(UsersService usersService)
    {
        this.usersService = usersService;
    }

    //Авторизация
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginDTO request
    )
    {
        AuthResponseDTO response =
                usersService.login(request);

        return ResponseEntity.ok(response);
    }

    //Обновление токена
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(
            @Valid @RequestBody RefreshTokenDTO request
    )
    {
        AuthResponseDTO response =
                usersService.refresh(request);

        return ResponseEntity.ok(response);
    }

    //Добавление пользователя
    @PostMapping("/addUser")
    public ResponseEntity<Boolean> createUser(
            @Valid @RequestBody CreateUserDTO request
    )
    {
        boolean created =
                usersService.addUser(request);

        return ResponseEntity.ok(created);
    }

    //Поиск пользователя по email
    @GetMapping("/findByEmail")
    public List<UserDTO> findUserByEmail(
            @RequestParam String email
    )
    {
        return usersService.findUserByEmail(email);
    }

    //Поиск пользователя по id
    @GetMapping("/findById")
    public List<UserDTO> findById(
            @RequestParam UUID id
    )
    {
        return usersService.findById(id);
    }

    //Поиск всех пользователей
    @GetMapping("/findAllUsers")
    public List<UserDTO> findAllUsers()
    {
        return usersService.findAllUsers();
    }

    //Обновление данных о пользователе
    @PostMapping("/updateUser")
    public boolean update(
            @Valid @RequestBody UpdateUserDTO request
    )
    {
        return usersService.update(
                request.getId(),
                request.getEmail(),
                request.getUsername(),
                request.getRoleId(),
                request.getPassword(),
                request.isEnabled()
        );
    }

    //Удалить пользователя по id
    @DeleteMapping("/deleteUser")
    public boolean delete(
            @RequestParam UUID id
    )
    {
        return usersService.delete(id);
    }

    //Получить все пользовательские роли системы
    @GetMapping("/findAllRoles")
    public List<RoleDTO> findAllRoles()
    {
        return usersService.findAllRoles();
    }

    //Выйти из системы
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenDTO request
    )
    {
        usersService.logout(request);

        return ResponseEntity.noContent().build();
    }
}
