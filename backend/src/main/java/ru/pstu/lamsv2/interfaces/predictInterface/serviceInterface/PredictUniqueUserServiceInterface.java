package ru.pstu.lamsv2.interfaces.predictInterface.serviceInterface;

import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;

import java.util.List;

/**
    Интерфейс для описания методов сервиса обновления прогноза по количеству уникальных пользователей системы. Включает в себя методы:
        1. Обновление таблицы данных с градацией по часам
        2. Обновление таблицы данных с градацией по дням
        3. Обновление таблицы данных с градацией по месяцам
*/

public interface PredictUniqueUserServiceInterface
{

    //Получение уникальных пользователей с агрегацией по часам
    List<DataFormatFromPredictDTO> predictGetUniqueUserWithHour(int hour);

    //Получение уникальных пользователей с агрегацией по дням
    List<DataFormatFromPredictDTO> predictGetUniqueUserWithDay(int days);

    //Получение уникальных пользователей с агрегацией по месяцам
    List<DataFormatFromPredictDTO> predictGetUniqueUserWithMonth(int month);
}
