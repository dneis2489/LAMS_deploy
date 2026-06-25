package ru.pstu.lamsv2.interfaces.userInterfaces;

import ru.pstu.lamsv2.dto.getDataInDB.userDTO.RoleDTO;

import java.util.List;

/**
    Интерфейс описывающий методы репозитория работы с ролями пользователей. Содержит методы:
        1. Найти все роли
        2. Проверить существование роли по её id
*/

//Интерфейс для методов репозитория работы с ролями пользователей
public interface RoleRepoInterface
{
    //Найти все роли
    List<RoleDTO> findAll();

    //Проверить существование роли по её id
    boolean existsById(Long id);
}
