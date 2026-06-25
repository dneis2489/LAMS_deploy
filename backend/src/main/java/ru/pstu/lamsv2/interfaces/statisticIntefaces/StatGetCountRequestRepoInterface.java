package ru.pstu.lamsv2.interfaces.statisticIntefaces;

import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.CountRequestForMethodsStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.CountRequestStatDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
    Интерфейс для описания методов репозитория получения данных для статистики по количеству запросов. Содержит методы:
        1. Получение статистики по количеству запросов по методам микросвервисов с агрегацией по часам/дням/месяцам
        2. Получение общего количества запросов к системе с агрегацией по часам/дням/месяцам
*/

public interface StatGetCountRequestRepoInterface
{
    //Получение количества запоросов по методам микросервисов с агрегацией по часам
    List<CountRequestForMethodsStatDTO> getCountRequestForMethodsWithHour(int hour);

    //Получение количества запоросов по методам микросервисов с агрегацией по дням
    List<CountRequestForMethodsStatDTO> getCountRequestForMethodsWithDay(int days);

    //Получение количества запоросов по методам микросервисов с агрегацией по месяцам
    List<CountRequestForMethodsStatDTO> getCountRequestForMethodsWithMonth(int month);

    //Получение общего количества запоросов с агрегацией по часам
    List<CountRequestStatDTO> getCountRequestWithHour(int hour);

    //Получение общего количества запоросов с агрегацией по дням
    List<CountRequestStatDTO> getCountRequestWithDay(int days);

    //Получение общего количества запоросов с агрегацией по месяцам
    List<CountRequestStatDTO> getCountRequestWithMonth(int month);

    void updateTotalCountRequestHourAnomalies(List<LocalDateTime> checkedDates, List<LocalDateTime> anomalyDates);

    void updateTotalCountRequestDayAnomalies(List<LocalDateTime> checkedDates, List<LocalDateTime> anomalyDates);

    void updateTotalCountRequestMonthAnomalies(List<LocalDateTime> checkedDates, List<LocalDateTime> anomalyDates);
}
