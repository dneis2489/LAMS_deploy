package ru.pstu.lamsv2.interfaces.statisticIntefaces;

import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.DurationForMethodsStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.DurationStatDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
    Интерфейс для описания методов репозитория получения данных для статистики по длительности выполнения запросов. Содержит методы:
        1. Получение статистики по длительности выполнения запросов для каждого микросевиса с агрегацией по часам/дням/месяцам
        2. Получение общей длительности выполнения запросов системой с агрегацией по часам/дням/месяцам
*/

public interface StatGetDurationRepoInterface
{
    //Получение длительности выполнения запоросов по методам микросервисов с агрегацией по часам
    List<DurationForMethodsStatDTO> getDurationForMethodsWithHour(int hour);

    //Получение длительности выполнения запоросов по методам микросервисов с агрегацией по дням
    List<DurationForMethodsStatDTO> getDurationForMethodsWithDay(int days);

    //Получение длительности выполнения запоросов по методам микросервисов с агрегацией по месяцам
    List<DurationForMethodsStatDTO> getDurationForMethodsWithMonth(int month);

    //Получение общей длительности выполнения запоросов с агрегацией по часам
    List<DurationStatDTO> getDurationWithHour(int hour);

    //Получение общей длительности выполнения запоросов с агрегацией по дням
    List<DurationStatDTO> getDurationWithDay(int days);

    //Получение общей длительности выполнения запоросов с агрегацией по месяцам
    List<DurationStatDTO> getDurationWithMonth(int month);

    void updateTotalDurationHourAnomalies(List<LocalDateTime> checkedDates, List<LocalDateTime> anomalyDates);

    void updateTotalDurationDayAnomalies(List<LocalDateTime> checkedDates, List<LocalDateTime> anomalyDates);

    void updateTotalDurationMonthAnomalies(List<LocalDateTime> checkedDates, List<LocalDateTime> anomalyDates);
}
