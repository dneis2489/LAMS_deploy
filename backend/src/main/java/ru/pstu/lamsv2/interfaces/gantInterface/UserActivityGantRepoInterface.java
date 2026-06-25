package ru.pstu.lamsv2.interfaces.gantInterface;

import ru.pstu.lamsv2.dto.getDataInDB.gantDTO.UserActivityRequestDTO;

import java.util.List;

/**
    Интерфейс для описания методов репозитория получения данных для диаграмы Ганта активности пользователей
*/

public interface UserActivityGantRepoInterface
{
    //Получение данных по активности пользователей с агрегацией по часам
    List<UserActivityRequestDTO> getUserActivityForGant();
}
