package ru.pstu.lamsv2.interfaces.userInterfaces;

import ru.pstu.lamsv2.dto.getDataInDB.userDTO.UserDTO;

import java.util.List;
import java.util.UUID;

/**
     Интерфейс описывающий методы репозитория работы с пользователями. Содержит методы:
        1. Найти пользователя по email
        2. Найти пользователя по id
        3. Получить всех пользователей в системе
        4. Проверить, есть ли такой email в системе
        5. Добавление нового пользователя
        6. Обновление данных о пользовтеле
        7. Удаление пользователя по его id
*/

//Интерфейс для методов репозитория работы с пользователями
public interface UserRepoInterface
{
    //Найти пользователя по email
    List<UserDTO> findUserByEmail(String email);

    //Найти пользователя по id
    List<UserDTO> findById(UUID id);

    //Получить всех пользователей в системе
    List<UserDTO> findAll();

    //Проверить, есть ли такой email в системе
    boolean existsByEmail(String email);

    //Добавление нового пользователя
    UUID create(String email, String username, String passwordHash, Long roleId);

    //Обновление данных о пользовтеле
    boolean update(UUID id, String email, String username, Long roleId, String passwordHash, boolean enabled);

    //Удаление пользователя по его id
    boolean delete(UUID id);
}
