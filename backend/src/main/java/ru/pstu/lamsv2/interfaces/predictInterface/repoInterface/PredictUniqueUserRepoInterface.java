package ru.pstu.lamsv2.interfaces.predictInterface.repoInterface;

import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;

import java.util.List;

/**
    Интерфейс для описания методов репозитория обновления прогноза по количеству уникальных пользователей системы. Включает в себя методы:
        1. Обновление таблицы данных с градацией по часам
        2. Обновление таблицы данных с градацией по дням
        3. Обновление таблицы данных с градацией по месяцам
*/

public interface PredictUniqueUserRepoInterface
{
    //Обновление прогноза по уникальным пользователям с агрегацией по часам
    void updateUniqueUserWithHourInDB(List<DataFormatFromPredictDTO> forecastList);

    //Обновление прогноза по уникальным пользователям с агрегацией по дням
    void updateUniqueUserWithDayInDB(List<DataFormatFromPredictDTO> forecastList);

    //Обновление прогноза по уникальным пользователям с агрегацией по месяцам
    void updateUniqueUserWithMonthInDB(List<DataFormatFromPredictDTO> forecastList);
}
