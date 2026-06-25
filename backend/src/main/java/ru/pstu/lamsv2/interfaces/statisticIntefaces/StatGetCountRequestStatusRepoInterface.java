package ru.pstu.lamsv2.interfaces.statisticIntefaces;

import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.CountStatusRequestForMethodsStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.CountStatusRequestStatDTO;

import java.util.List;

/**
    Интерфейс для описания методов репозитория получения данных для статистики по статусам ответов. Содержит методы:
        1. Получение статистики по статусам ответов для каждого микросервиса с агрегацией по часам/дням/месяцам
        2. Получение общего количества статусов ответов к системе с агрегацией по часам/дням/месяцам
*/

public interface StatGetCountRequestStatusRepoInterface
{
    //Получение количества типов ответа по методам микросервисов с агрегацией по часам
    List<CountStatusRequestForMethodsStatDTO> getCountRequestStatusForMethodsWithHour(int hour);

    //Получение количества типов ответа по методам микросервисов с агрегацией по дням
    List<CountStatusRequestForMethodsStatDTO> getCountRequestStatusForMethodsWithDay(int days);

    //Получение количества типов ответа по методам микросервисов с агрегацией по месяцам
    List<CountStatusRequestForMethodsStatDTO> getCountRequestStatusForMethodsMonth(int month);

    //Получение общего количества типов ответа с агрегацией по часам
    List<CountStatusRequestStatDTO> getCountRequestStatusWithHour(int hour);

    //Получение общего количества типов ответа с агрегацией по дням
    List<CountStatusRequestStatDTO> getCountRequestStatusWithDay(int days);

    //Получение общего количества типов ответа с агрегацией по месяцам
    List<CountStatusRequestStatDTO> getCountRequestStatusMonth(int month);

    void updateTotalRequestStatusHourAnomalies(List<CountStatusRequestStatDTO> checkedRows, List<CountStatusRequestStatDTO> anomalyRows);

    void updateTotalRequestStatusDayAnomalies(List<CountStatusRequestStatDTO> checkedRows, List<CountStatusRequestStatDTO> anomalyRows);

    void updateTotalRequestStatusMonthAnomalies(List<CountStatusRequestStatDTO> checkedRows, List<CountStatusRequestStatDTO> anomalyRows);
}
