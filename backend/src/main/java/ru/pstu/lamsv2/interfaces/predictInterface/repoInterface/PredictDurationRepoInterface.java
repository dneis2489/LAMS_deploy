package ru.pstu.lamsv2.interfaces.predictInterface.repoInterface;

import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;

import java.util.List;

/**
    Интерфейс для описания методов репозитория обновления прогноза по длительности выполнения запросов. Включает в себя методы:
        1. Обновление таблицы данных с градацией по часам
        2. Обновление таблицы данных с градацией по дням
        3. Обновление таблицы данных с градацией по месяцам
*/

public interface PredictDurationRepoInterface
{
    //Обновление прогноза по длительности выполнения запросов с агрегацией по часам
    void updateTotalDurationWithHourInDB(List<DataFormatFromPredictDTO> forecastList);

    //Обновление прогноза по длительности выполнения запросов с агрегацией по дням
    void updateTotalDurationWithDayInDB(List<DataFormatFromPredictDTO> forecastList);

    //Обновление прогноза по длительности выполнения запросов с агрегацией по месяцам
    void updateTotalDurationWithMonthInDB(List<DataFormatFromPredictDTO> forecastList);
}
