package ru.pstu.lamsv2.interfaces.statisticIntefaces;

import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.UniqueUsersForMethodStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.UniqueUsersStatDTO;

import java.util.List;

/**
    Интерфейс для описания методов репозитория получения данных для статистики по уникальным пользователям. Содержит методы:
        1. Получение перечня и количества уникальных пользователей системы по методам микросервисов с агрегацией по часам/дням/месяца
        2. Получение перечня и количества уникальных пользователей системы с агрегацией по часам/дням/месяцам
*/

public interface StatGetUniqueUserRepoInterface
{
    //Получение уникальных пользователей по методам микросервисов с агрегацией по часам
    List<UniqueUsersForMethodStatDTO> getUniqueUserForMethodsWithHour(int hour);

    //Получение уникальных пользователей по методам микросервисов с агрегацией по дням
    List<UniqueUsersForMethodStatDTO> getUniqueUserForMethodsWithDay(int days);

    //Получение уникальных пользователей по методам микросервисов с агрегацией по месяцам
    List<UniqueUsersForMethodStatDTO> getUniqueUserForMethodsWithMonth(int month);

    //Получение уникальных пользователей с агрегацией по часам
    List<UniqueUsersStatDTO> getUniqueUserWithHour(int hour);

    //Получение уникальных пользователей с агрегацией по дням
    List<UniqueUsersStatDTO> getUniqueUserWithDay(int days);

    //Получение уникальных пользователей с агрегацией по месяцам
    List<UniqueUsersStatDTO> getUniqueUserWithMonth(int month);
}
