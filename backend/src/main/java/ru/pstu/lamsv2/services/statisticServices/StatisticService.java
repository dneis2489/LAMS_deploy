package ru.pstu.lamsv2.services.statisticServices;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.application.statisticDTO.uniqueUsersForMethodsAggregation.UniqueUsersConvertDataDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.UniqueUsersForMethodStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.CountRequestStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.DurationStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.UniqueUsersStatDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.countRequestForMethodsAggregation.CountRequestConvertDataDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.durationForMethodsAggregation.DurationConvertDataDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForMethodsAggregation.RequestStatusConvertDataDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForTotalStat.RequestStatusConvertDataForTotalDTO;
import ru.pstu.lamsv2.interfaces.statisticIntefaces.*;
import ru.pstu.lamsv2.subMethods.convertData.converForWeb.*;

import java.util.List;

/**
    Сервис для реализации методов статистики. Данный интерфейс содержит следующие методы:
        1. Получение статистики по количеству запросов по методам микросвервисов с агрегацией по часам/дням/месяцам
        2. Получение общего количества запросов к системе с агрегацией по часам/дням/месяцам
        3. Получение статистики по статусам ответов для каждого микросервиса с агрегацией по часам/дням/месяцам
        4. Получение общего количества статусов ответов к системе с агрегацией по часам/дням/месяцам
        5. Получение статистики по длительности выполнения запросов для каждого микросевиса с агрегацией по часам/дням/месяцам
        6. Получение общей длительности выполнения запросов системой с агрегацией по часам/дням/месяцам
        7. Получение перечня уникальных пользователей системы по методам микросервисов с агрегацией по часам/дням/месяцам
        8. Получение перечня уникальных пользователей системы с агрегацией по часам/дням/месяцам
*/

@Service
public class StatisticService implements StatServiceInterface
{
    private final StatGetCountRequestRepoInterface statisticGetCountRequestRepositoryInterface;
    private final StatGetCountRequestStatusRepoInterface statGetCountRequestStatusRepositoryInterface;
    private final StatGetDurationRepoInterface statGetDurationRepositoryInterface;
    private final StatGetUniqueUserRepoInterface  statGetUniqueUserRepositoryInterface;

    public StatisticService(
            StatGetCountRequestRepoInterface statisticGetCountRequestRepositoryInterface,
            StatGetCountRequestStatusRepoInterface statGetCountRequestStatusRepositoryInterface,
            StatGetDurationRepoInterface statGetDurationRepositoryInterface,
            StatGetUniqueUserRepoInterface statGetUniqueUserRepositoryInterface)
    {
        this.statisticGetCountRequestRepositoryInterface = statisticGetCountRequestRepositoryInterface;
        this.statGetCountRequestStatusRepositoryInterface = statGetCountRequestStatusRepositoryInterface;
        this.statGetDurationRepositoryInterface = statGetDurationRepositoryInterface;
        this.statGetUniqueUserRepositoryInterface = statGetUniqueUserRepositoryInterface;
    }

    /*----------------------------------------Статистика по количеству запросов----------------------------------------*/
    //Получение количества запоросов по методам микросервисов с агрегацией по часам
    @Override
    public List<CountRequestConvertDataDTO> getCountRequestForMethodsWithHour(int hour)
    {
        return ConvertCountRequestStatData.convertCountRequestStats(
                statisticGetCountRequestRepositoryInterface.getCountRequestForMethodsWithHour(hour)
        );
    }

    //Получение количества запоросов по методам микросервисов с агрегацией по дням
    @Override
    public List<CountRequestConvertDataDTO> getCountRequestForMethodsWithDay(int days)
    {
        return ConvertCountRequestStatData.convertCountRequestStats(
                statisticGetCountRequestRepositoryInterface.getCountRequestForMethodsWithDay(days)
        );
    }

    //Получение количества запоросов по методам микросервисов с агрегацией по месяцам
    @Override
    public List<CountRequestConvertDataDTO> getCountRequestForMethodsWithMonth(int month)
    {
        return ConvertCountRequestStatData.convertCountRequestStats(
                statisticGetCountRequestRepositoryInterface.getCountRequestForMethodsWithMonth(month)
        );
    }

    //Получение общего количества запоросов с агрегацией по часам
    @Override
    public List<CountRequestStatDTO> getCountRequestWithHour(int hour)
    {
        return statisticGetCountRequestRepositoryInterface.getCountRequestWithHour(hour);
    }

    //Получение общего количества запоросов с агрегацией по дням
    @Override
    public List<CountRequestStatDTO> getCountRequestWithDay(int days)
    {
        return statisticGetCountRequestRepositoryInterface.getCountRequestWithDay(days);
    }

    //Получение общего количества запоросов с агрегацией по месяцам
    @Override
    public List<CountRequestStatDTO> getCountRequestWithMonth(int month)
    {
        return statisticGetCountRequestRepositoryInterface.getCountRequestWithMonth(month);
    }

    /*----------------------------------------Статистика по типам ответов----------------------------------------*/
    //Получение количества типов ответа по методам микросервисов с агрегацией по часам
    @Override
    public List<RequestStatusConvertDataDTO> getCountRequestStatusForMethodsWithHour(int hour)
    {
        return ConvertRequestStatusStatData.convertRequestStatusStats(
                statGetCountRequestStatusRepositoryInterface.getCountRequestStatusForMethodsWithHour(hour)
        );
    }

    //Получение общего количества запоросов с агрегацией по месяцам
    @Override
    public List<RequestStatusConvertDataDTO> getCountRequestStatusForMethodsWithDay(int days)
    {
        return ConvertRequestStatusStatData.convertRequestStatusStats(
                statGetCountRequestStatusRepositoryInterface.getCountRequestStatusForMethodsWithDay(days)
        );
    }

    //Получение общего количества запоросов с агрегацией по месяцам
    @Override
    public List<RequestStatusConvertDataDTO> getCountRequestStatusForMethodsMonth(int month)
    {
        return ConvertRequestStatusStatData.convertRequestStatusStats(
                statGetCountRequestStatusRepositoryInterface.getCountRequestStatusForMethodsMonth(month)
        );
    }

    //Получение общего количества запоросов с агрегацией по месяцам
    @Override
    public List<RequestStatusConvertDataForTotalDTO> getCountRequestStatusWithHour(int hour)
    {
        return ConvertCountRequestTotalStatData.convertCountRequestStats(
                statGetCountRequestStatusRepositoryInterface.getCountRequestStatusWithHour(hour)
        );
    }

    //Получение общего количества запоросов с агрегацией по месяцам
    @Override
    public List<RequestStatusConvertDataForTotalDTO> getCountRequestStatusWithDay(int days)
    {
        return ConvertCountRequestTotalStatData.convertCountRequestStats(
                statGetCountRequestStatusRepositoryInterface.getCountRequestStatusWithDay(days)
        );
    }

    //Получение общего количества запоросов с агрегацией по месяцам
    @Override
    public List<RequestStatusConvertDataForTotalDTO> getCountRequestStatusMonth(int month)
    {
        return ConvertCountRequestTotalStatData.convertCountRequestStats(
                statGetCountRequestStatusRepositoryInterface.getCountRequestStatusMonth(month)
        );
    }

    /*----------------------------------------Статистика по длительности выполнения запросов----------------------------------------*/
    //Получение количества типов ответа по методам микросервисов с агрегацией по часам
    @Override
    public List<DurationConvertDataDTO> getDurationForMethodsWithHour(int hour)
    {
        return ConvertDurationStatData.convertDurationStats(
                statGetDurationRepositoryInterface.getDurationForMethodsWithHour(hour)
        );
    }

    //Получение общего количества запоросов с агрегацией по месяцам
    @Override
    public List<DurationConvertDataDTO> getDurationForMethodsWithDay(int days)
    {
        return ConvertDurationStatData.convertDurationStats(
                statGetDurationRepositoryInterface.getDurationForMethodsWithDay(days)
        );
    }

    //Получение общего количества запоросов с агрегацией по месяцам
    @Override
    public List<DurationConvertDataDTO> getDurationForMethodsWithMonth(int month)
    {
        return ConvertDurationStatData.convertDurationStats(
                statGetDurationRepositoryInterface.getDurationForMethodsWithMonth(month)
        );
    }

    //Получение общего количества запоросов с агрегацией по месяцам
    @Override
    public List<DurationStatDTO> getDurationWithHour(int hour)
    {
        return statGetDurationRepositoryInterface.getDurationWithHour(hour);
    }

    //Получение общего количества запоросов с агрегацией по месяцам
    @Override
    public List<DurationStatDTO> getDurationWithDay(int days)
    {
        return statGetDurationRepositoryInterface.getDurationWithDay(days);
    }

    //Получение общего количества запоросов с агрегацией по месяцам
    @Override
    public List<DurationStatDTO> getDurationWithMonth(int month)
    {
        return statGetDurationRepositoryInterface.getDurationWithMonth(month);
    }

    /*----------------------------------------Статистика по количеству уникальных пользователей----------------------------------------*/
    //Получение уникальных пользователей по методам микросервисов с агрегацией по часам
    @Override
    public List<UniqueUsersConvertDataDTO> getUniqueUserForMethodsWithHour(int hour) {
        return ConvertUniqueUsersStatData.convertUniqueUsersStat(
                statGetUniqueUserRepositoryInterface.getUniqueUserForMethodsWithHour(hour)
        );
    }

    //Получение уникальных пользователей по методам микросервисов с агрегацией по дням
    @Override
    public List<UniqueUsersConvertDataDTO> getUniqueUserForMethodsWithDay(int days) {
        return ConvertUniqueUsersStatData.convertUniqueUsersStat(
                statGetUniqueUserRepositoryInterface.getUniqueUserForMethodsWithDay(days)
        );
    }

    //Получение уникальных пользователей по методам микросервисов с агрегацией по месяцам
    @Override
    public List<UniqueUsersConvertDataDTO> getUniqueUserForMethodsWithMonth(int month) {
        return ConvertUniqueUsersStatData.convertUniqueUsersStat(
                statGetUniqueUserRepositoryInterface.getUniqueUserForMethodsWithMonth(month)
        );
    }

    //Получение общего уникальных пользователей с агрегацией по часам
    @Override
    public List<UniqueUsersStatDTO> getUniqueUserWithHour(int hour)
    {
        return statGetUniqueUserRepositoryInterface.getUniqueUserWithHour(hour);
    }

    //Получение общего уникальных пользователей с агрегацией по дням
    @Override
    public List<UniqueUsersStatDTO> getUniqueUserWithDay(int days)
    {
        return statGetUniqueUserRepositoryInterface.getUniqueUserWithDay(days);
    }

    //Получение общего уникальных пользователей с агрегацией по месяцам
    @Override
    public List<UniqueUsersStatDTO> getUniqueUserWithMonth(int month)
    {
        return statGetUniqueUserRepositoryInterface.getUniqueUserWithMonth(month);
    }
}
