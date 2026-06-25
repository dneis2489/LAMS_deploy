package ru.pstu.lamsv2.interfaces.userInterfaces;

import ru.pstu.lamsv2.dto.application.userDTO.AuthResponseDTO;
import ru.pstu.lamsv2.dto.application.userDTO.CreateUserDTO;
import ru.pstu.lamsv2.dto.application.userDTO.LoginDTO;
import ru.pstu.lamsv2.dto.application.userDTO.RefreshTokenDTO;
import ru.pstu.lamsv2.dto.getDataInDB.userDTO.*;

import java.util.List;
import java.util.UUID;

/**
    Интерфейс описывающий методы сервиса работы с пользователями. Содержит методы:
        1. Авторизация пользователя
        2. Обновление токена пользователя
        3. Добавление нового пользователя. Возвращает true при удаче
        4. Найти пользователя по email
        5. Найти пользователя по id
        6. Получить всех пользователей в зарегистрированных в системе
        7. Обновление данных о пользователе
        8. Удаление пользователя по его id
        9. Получить перечень всех системных ролей
*/

public interface UsersServiceInterface
{
    //Авторизация пользователя
    AuthResponseDTO login(LoginDTO request);

    //Обновление токена пользователя
    AuthResponseDTO refresh(RefreshTokenDTO request);

    //Добавление нового пользователя. Возвращает true при удаче
    boolean addUser(CreateUserDTO request);

    //Найти пользователя по email
    List<UserDTO> findUserByEmail(String email);

    //Найти пользователя по id
    List<UserDTO> findById(UUID id);

    //Получить всех пользователей в зарегистрированных в системе
    List<UserDTO> findAllUsers();

    //Обновление данных о пользователе
    boolean update(UUID id, String email, String username, Long roleId, String password, boolean enabled);

    //Удаление пользователя по его id
    boolean delete(UUID id);

    //Получить перечень всех системных ролей
    List<RoleDTO> findAllRoles();
}
