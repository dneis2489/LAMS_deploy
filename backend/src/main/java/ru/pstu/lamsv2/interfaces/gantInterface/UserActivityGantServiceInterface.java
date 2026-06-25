package ru.pstu.lamsv2.interfaces.gantInterface;

import ru.pstu.lamsv2.dto.application.gantDTO.UserActivityGrouppingDTO;

import java.util.List;

/**
    Интерфейс для описания методов сервиса получения данных для диаграмы Ганта активности пользователей
*/

public interface UserActivityGantServiceInterface
{
    //Получение данных по активности пользователей с агрегацией по часам
    List<UserActivityGrouppingDTO> getUserActivityForGant();
}
