package ru.pstu.lamsv2.interfaces.predictInterface.repoInterface;

import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;

import java.util.List;
import java.util.Map;

/**
    Интерфейс для описания методов репозитория обновления прогноза по общему количеству статусов запросов. Включает в себя методы:
        1. Обновление таблицы данных с градацией по часам
        2. Обновление таблицы данных с градацией по дням
        3. Обновление таблицы данных с градацией по месяцам
*/

public interface PredictRequestStatusRepoInterface
{
    //Обновление прогноза по статусам выполнения запросов с агрегацией по часам
    void updateTotalRequestStatusWithHourInDB(Map<Integer, List<DataFormatFromPredictDTO>> forecastList);

    //Обновление прогноза по статусам выполнения запросов с агрегацией по дням
    void updateTotalRequestStatusWithDayInDB(Map<Integer, List<DataFormatFromPredictDTO>> forecastList);

    //Обновление прогноза по статусам выполнения запросов с агрегацией по месяцам
    void updateTotalRequestStatusWithMonthInDB(Map<Integer, List<DataFormatFromPredictDTO>> forecastList);
}
