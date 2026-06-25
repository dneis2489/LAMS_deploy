package ru.pstu.lamsv2.interfaces.predictInterface.serviceInterface;

import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;

import java.util.List;
import java.util.Map;

/**
    Интерфейс для описания методов сервиса обновления прогноза по общему количеству статусов запросов. Включает в себя методы:
        1. Обновление таблицы данных с градацией по часам
        2. Обновление таблицы данных с градацией по дням
        3. Обновление таблицы данных с градацией по месяцам
*/

public interface PredictRequestStatusServiceInterface
{
    //Прогнозирование общего количества запросов с агрегацией по часам
    Map<Integer, List<DataFormatFromPredictDTO>> predictRequestStatusWithHour(int hour);

    //Прогнозирование общего количества запросов с агрегацией по дням
    Map<Integer, List<DataFormatFromPredictDTO>> predictRequestStatusWithDay(int days);

    //Прогнозирование общего количества запросов с агрегацией по месяцам
    Map<Integer, List<DataFormatFromPredictDTO>> predictRequestStatusWithMonth(int month);
}
