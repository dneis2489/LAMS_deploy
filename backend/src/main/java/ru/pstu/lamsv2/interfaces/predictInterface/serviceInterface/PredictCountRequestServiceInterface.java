package ru.pstu.lamsv2.interfaces.predictInterface.serviceInterface;

import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;

import java.util.List;

/**
    Интерфейс для описания методов сервиса обновления прогноза по общему количеству запросов. Включает в себя методы:
        1. Обновление таблицы данных с градацией по часам
        2. Обновление таблицы данных с градацией по дням
        3. Обновление таблицы данных с градацией по месяцам
*/

public interface PredictCountRequestServiceInterface
{
    /*----------------------------------------Статистика по количеству запросов----------------------------------------*/
    //Прогнозирование общего количества запросов с агрегацией по часам
    List<DataFormatFromPredictDTO> predictCountRequestWithHour(int hour);

    //Прогнозирование общего количества запросов с агрегацией по дням
    List<DataFormatFromPredictDTO> predictCountRequestWithDay(int days);

    //Прогнозирование общего количества запросов с агрегацией по месяцам
    List<DataFormatFromPredictDTO> predictCountRequestWithMonth(int month);
}
