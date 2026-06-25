package ru.pstu.lamsv2.interfaces.statisticIntefaces;

import ru.pstu.lamsv2.dto.application.statisticDTO.uniqueUsersForMethodsAggregation.UniqueUsersConvertDataDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.UniqueUsersForMethodStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.CountRequestStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.DurationStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.UniqueUsersStatDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.countRequestForMethodsAggregation.CountRequestConvertDataDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.durationForMethodsAggregation.DurationConvertDataDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForMethodsAggregation.RequestStatusConvertDataDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForTotalStat.RequestStatusConvertDataForTotalDTO;

import java.util.List;

/**
    Интерфейс для описания методов сервиса статистики. Данный интерфейс содержит следующие методы:
        1. Получение статистики по количеству запросов по методам микросвервисов с агрегацией по часам/дням/месяцам
        2. Получение общего количества запросов к системе с агрегацией по часам/дням/месяцам
        3. Получение статистики по статусам ответов для каждого микросервиса с агрегацией по часам/дням/месяцам
        4. Получение общего количества статусов ответов к системе с агрегацией по часам/дням/месяцам
        5. Получение статистики по длительности выполнения запросов для каждого микросевиса с агрегацией по часам/дням/месяцам
        6. Получение общей длительности выполнения запросов системой с агрегацией по часам/дням/месяцам
        7. Получение перечня уникальных пользователей системы по методам микросервисов с агрегацией по часам/дням/месяцам
        8. Получение перечня уникальных пользователей системы с агрегацией по часам/дням/месяцам
*/

public interface StatServiceInterface
{
    /*----------------------------------------Статистика по количеству запросов----------------------------------------*/
    //Получение количества запоросов по методам микросервисов с агрегацией по часам
    List<CountRequestConvertDataDTO> getCountRequestForMethodsWithHour(int hour);

    //Получение количества запоросов по методам микросервисов с агрегацией по дням
    List<CountRequestConvertDataDTO> getCountRequestForMethodsWithDay(int days);

    //Получение количества запоросов по методам микросервисов с агрегацией по месяцам
    List<CountRequestConvertDataDTO> getCountRequestForMethodsWithMonth(int month);

    //Получение общего количества запоросов с агрегацией по часам
    List<CountRequestStatDTO> getCountRequestWithHour(int hour);

    //Получение общего количества запоросов с агрегацией по дням
    List<CountRequestStatDTO> getCountRequestWithDay(int days);

    //Получение общего количества запоросов с агрегацией по месяцам
    List<CountRequestStatDTO> getCountRequestWithMonth(int month);

    /*----------------------------------------Статистика по типам ответов----------------------------------------*/
    //Получение количества типов ответа по методам микросервисов с агрегацией по часам
    List<RequestStatusConvertDataDTO> getCountRequestStatusForMethodsWithHour(int hour);

    //Получение количества типов ответа по методам микросервисов с агрегацией по дням
    List<RequestStatusConvertDataDTO> getCountRequestStatusForMethodsWithDay(int days);

    //Получение количества типов ответа по методам микросервисов с агрегацией по месяцам
    List<RequestStatusConvertDataDTO> getCountRequestStatusForMethodsMonth(int month);

    //Получение общего количества типов ответа с агрегацией по часам
    List<RequestStatusConvertDataForTotalDTO> getCountRequestStatusWithHour(int hour);

    //Получение общего количества типов ответа с агрегацией по дням
    List<RequestStatusConvertDataForTotalDTO> getCountRequestStatusWithDay(int days);

    //Получение общего количества типов ответа с агрегацией по месяцам
    List<RequestStatusConvertDataForTotalDTO> getCountRequestStatusMonth(int month);

    /*----------------------------------------Статистика по длительности выполнения запросов----------------------------------------*/
    //Получение длительности выполнения запоросов по методам микросервисов с агрегацией по часам
    List<DurationConvertDataDTO> getDurationForMethodsWithHour(int hour);

    //Получение длительности выполнения запоросов по методам микросервисов с агрегацией по дням
    List<DurationConvertDataDTO> getDurationForMethodsWithDay(int days);

    //Получение длительности выполнения запоросов по методам микросервисов с агрегацией по месяцам
    List<DurationConvertDataDTO> getDurationForMethodsWithMonth(int month);

    //Получение общей длительности выполнения запоросов с агрегацией по часам
    List<DurationStatDTO> getDurationWithHour(int hour);

    //Получение общей длительности выполнения запоросов с агрегацией по дням
    List<DurationStatDTO> getDurationWithDay(int days);

    //Получение общей длительности выполнения запоросов с агрегацией по месяцам
    List<DurationStatDTO> getDurationWithMonth(int month);

    /*----------------------------------------Статистика по количеству уникальных пользователей----------------------------------------*/
    //Получение уникальных пользователей по методам микросервисов с агрегацией по часам
    List<UniqueUsersConvertDataDTO> getUniqueUserForMethodsWithHour(int hour);

    //Получение уникальных пользователей по методам микросервисов с агрегацией по дням
    List<UniqueUsersConvertDataDTO> getUniqueUserForMethodsWithDay(int days);

    //Получение уникальных пользователей по методам микросервисов с агрегацией по месяцам
    List<UniqueUsersConvertDataDTO> getUniqueUserForMethodsWithMonth(int month);

    //Получение уникальных пользователей с агрегацией по часам
    List<UniqueUsersStatDTO> getUniqueUserWithHour(int hour);

    //Получение уникальных пользователей с агрегацией по дням
    List<UniqueUsersStatDTO> getUniqueUserWithDay(int days);

    //Получение уникальных пользователей с агрегацией по месяцам
    List<UniqueUsersStatDTO> getUniqueUserWithMonth(int month);
}
