package ru.pstu.lamsv2.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pstu.lamsv2.dto.application.statisticDTO.uniqueUsersForMethodsAggregation.UniqueUsersConvertDataDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.CountRequestStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.DurationStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.UniqueUsersStatDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.countRequestForMethodsAggregation.CountRequestConvertDataDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.durationForMethodsAggregation.DurationConvertDataDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForMethodsAggregation.RequestStatusConvertDataDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForTotalStat.RequestStatusConvertDataForTotalDTO;
import ru.pstu.lamsv2.enums.AggregationType;
import ru.pstu.lamsv2.interfaces.statisticIntefaces.StatServiceInterface;
import ru.pstu.lamsv2.subMethods.methodsForEnums.LengthPeriodForAggregation;

import java.util.List;

/**
    Контроллер получения статистики. Данный контроллер реализует следующие эндпоинты:
        1. Получение статистики по количеству запросов по методам микросвервисов с агрегацией по часам/дням/месяцам
        2. Получение общего количества запросов к системе с агрегацией по часам/дням/месяцам
        3. Получение статистики по статусам ответов для каждого микросервиса с агрегацией по часам/дням/месяцам
        4. Получение общего количества статусов ответов к системе с агрегацией по часам/дням/месяцам
        5. Получение статистики по длительности выполнения запросов для каждого микросевиса с агрегацией по часам/дням/месяцам
        6. Получение общей длительности выполнения запросов системой с агрегацией по часам/дням/месяцам
        7. Получение перечня уникальных пользователей системы по методам микросервисов с агрегацией по часам/дням/месяцам
        8. Получение перечня уникальных пользователей системы с агрегацией по часам/дням/месяцам
*/

@RestController
@RequestMapping("lams")
public class StatisticController
{
    private final StatServiceInterface statisticServiceInterface;

    public StatisticController(StatServiceInterface statisticServiceInterface)
    {
        this.statisticServiceInterface = statisticServiceInterface;
    }

    /*----------------------------------------Статистика по количеству запросов----------------------------------------*/
    //Получение количества запоросов по методам микросервисов с агрегацией по часам
    @GetMapping("/getCountRequestForMethodsWithHour")
    public List<CountRequestConvertDataDTO> getCountRequestForMethodsWithHour()
    {
        return  statisticServiceInterface.getCountRequestForMethodsWithHour(
                LengthPeriodForAggregation.getLength(AggregationType.HOURLY));
    }

    //Получение количества запоросов по методам микросервисов с агрегацией по дням
    @GetMapping("/getCountRequestForMethodsWithDay")
    public List<CountRequestConvertDataDTO> getCountRequestForMethodsWithDay()
    {
        return  statisticServiceInterface.getCountRequestForMethodsWithDay(
                LengthPeriodForAggregation.getLength(AggregationType.DAILY));
    }

    //Получение количества запоросов по методам микросервисов с агрегацией по месяцам
    @GetMapping("/getCountRequestForMethodsWithMonth")
    public List<CountRequestConvertDataDTO> getCountRequestForMethodsWithMonth()
    {
        return  statisticServiceInterface.getCountRequestForMethodsWithMonth(
                LengthPeriodForAggregation.getLength(AggregationType.MONTHLY));
    }

    //Получение общего количества запоросов с агрегацией по часам
    @GetMapping("/getCountRequestWithHour")
    public List<CountRequestStatDTO> getCountRequestWithHour()
    {
        return  statisticServiceInterface.getCountRequestWithHour(
                LengthPeriodForAggregation.getLength(AggregationType.HOURLY));
    }

    //Получение общего количества запоросов с агрегацией по дням
    @GetMapping("/getCountRequestWithDay")
    public List<CountRequestStatDTO> getCountRequestWithDay()
    {
        return  statisticServiceInterface.getCountRequestWithDay(
                LengthPeriodForAggregation.getLength(AggregationType.DAILY));
    }

    //Получение общего количества запоросов с агрегацией по месяцам
    @GetMapping("/getCountRequestWithMounth")
    public List<CountRequestStatDTO> getCountRequestWithMounth()
    {
        return  statisticServiceInterface.getCountRequestWithMonth(
                LengthPeriodForAggregation.getLength(AggregationType.MONTHLY));
    }

    /*----------------------------------------Статистика по типам ответов----------------------------------------*/
    //Получение количества типов ответа по методам микросервисов с агрегацией по часам
    @GetMapping("/getCountRequestStatusForMethodsWithHour")
    public List<RequestStatusConvertDataDTO> getCountRequestStatusForMethodsWithHour()
    {
        return  statisticServiceInterface.getCountRequestStatusForMethodsWithHour(
                LengthPeriodForAggregation.getLength(AggregationType.HOURLY));
    }

    //Получение количества типов ответа по методам микросервисов с агрегацией по дням
    @GetMapping("/getCountRequestStatusForMethodsWithDay")
    public List<RequestStatusConvertDataDTO> getCountRequestStatusForMethodsWithDay()
    {
        return  statisticServiceInterface.getCountRequestStatusForMethodsWithDay(
                LengthPeriodForAggregation.getLength(AggregationType.DAILY));
    }

    //Получение количества типов ответа по методам микросервисов с агрегацией по месяцам
    @GetMapping("/getCountRequestStatusForMethodsMonth")
    public List<RequestStatusConvertDataDTO> getCountRequestStatusForMethodsMonth()
    {
        return  statisticServiceInterface.getCountRequestStatusForMethodsMonth(
                LengthPeriodForAggregation.getLength(AggregationType.MONTHLY));
    }

    //Получение общего количества типов ответа с агрегацией по часам
    @GetMapping("/getCountRequestStatusWithHour")
    public List<RequestStatusConvertDataForTotalDTO> getCountRequestStatusWithHour()
    {
        return  statisticServiceInterface.getCountRequestStatusWithHour(
                LengthPeriodForAggregation.getLength(AggregationType.HOURLY));
    }

    //Получение общего количества типов ответа с агрегацией по дням
    @GetMapping("/getCountRequestStatusWithDay")
    public List<RequestStatusConvertDataForTotalDTO> getCountRequestStatusWithDay()
    {
        return  statisticServiceInterface.getCountRequestStatusWithDay(
                LengthPeriodForAggregation.getLength(AggregationType.DAILY));
    }

    //Получение общего количества типов ответа с агрегацией по месяцам
    @GetMapping("/getCountRequestStatusMonth")
    public List<RequestStatusConvertDataForTotalDTO> getCountRequestStatusMonth()
    {
        return  statisticServiceInterface.getCountRequestStatusMonth(
                LengthPeriodForAggregation.getLength(AggregationType.MONTHLY));
    }

    /*----------------------------------------Статистика по длительности выполнения запросов----------------------------------------*/
    //Получение количества типов ответа по методам микросервисов с агрегацией по часам
    @GetMapping("/getDurationForMethodsWithHour")
    public List<DurationConvertDataDTO> getDurationForMethodsWithHour()
    {
        return  statisticServiceInterface.getDurationForMethodsWithHour(
                LengthPeriodForAggregation.getLength(AggregationType.HOURLY));
    }

    //Получение количества типов ответа по методам микросервисов с агрегацией по дням
    @GetMapping("/getDurationForMethodsWithDay")
    public List<DurationConvertDataDTO> getDurationForMethodsWithDay()
    {
        return  statisticServiceInterface.getDurationForMethodsWithDay(
                LengthPeriodForAggregation.getLength(AggregationType.DAILY));
    }

    //Получение количества типов ответа по методам микросервисов с агрегацией по месяцам
    @GetMapping("/getDurationForMethodsWithMonth")
    public List<DurationConvertDataDTO> getDurationForMethodsWithMonth()
    {
        return  statisticServiceInterface.getDurationForMethodsWithMonth(
                LengthPeriodForAggregation.getLength(AggregationType.MONTHLY));
    }

    //Получение общего количества типов ответа с агрегацией по часам
    @GetMapping("/getDurationWithHour")
    public List<DurationStatDTO> getDurationWithHour()
    {
        return  statisticServiceInterface.getDurationWithHour(
                LengthPeriodForAggregation.getLength(AggregationType.HOURLY));
    }

    //Получение общего количества типов ответа с агрегацией по дням
    @GetMapping("/getDurationWithDay")
    public List<DurationStatDTO> getDurationWithDay()
    {
        return  statisticServiceInterface.getDurationWithDay(
                LengthPeriodForAggregation.getLength(AggregationType.DAILY));
    }

    //Получение общего количества типов ответа с агрегацией по месяцам
    @GetMapping("/getDurationWithMonth")
    public List<DurationStatDTO> getDurationWithMonth()
    {
        return  statisticServiceInterface.getDurationWithMonth(
                LengthPeriodForAggregation.getLength(AggregationType.MONTHLY));
    }

    /*----------------------------------------Статистика по количеству уникальных пользователей----------------------------------------*/
    @GetMapping("/getUniqueUserForMethodsWithHour")
    public List<UniqueUsersConvertDataDTO> getUniqueUserForMethodsWithHour()
    {
        return statisticServiceInterface.getUniqueUserForMethodsWithHour(
                LengthPeriodForAggregation.getLength(AggregationType.HOURLY));
    }

    @GetMapping("/getUniqueUserForMethodsWithDay")
    public List<UniqueUsersConvertDataDTO> getUniqueUserForMethodsWithDay()
    {
        return statisticServiceInterface.getUniqueUserForMethodsWithDay(
                LengthPeriodForAggregation.getLength(AggregationType.DAILY));
    }

    @GetMapping("/getUniqueUserForMethodsWithMonth")
    public List<UniqueUsersConvertDataDTO> getUniqueUserForMethodsWithMonth()
    {
        return statisticServiceInterface.getUniqueUserForMethodsWithMonth(
                LengthPeriodForAggregation.getLength(AggregationType.MONTHLY));
    }

    @GetMapping("/getUniqueUserWithHour")
    public List<UniqueUsersStatDTO> getUniqueUserWithHour()
    {
        return statisticServiceInterface.getUniqueUserWithHour(
                LengthPeriodForAggregation.getLength(AggregationType.HOURLY));
    }

    @GetMapping("/getUniqueUserWithDay")
    public List<UniqueUsersStatDTO> getUniqueUserWithDay()
    {
        return statisticServiceInterface.getUniqueUserWithDay(
                LengthPeriodForAggregation.getLength(AggregationType.DAILY));
    }

    @GetMapping("/getUniqueUserWithMonth")
    public List<UniqueUsersStatDTO> getUniqueUserWithMonth()
    {
        return statisticServiceInterface.getUniqueUserWithMonth(
                LengthPeriodForAggregation.getLength(AggregationType.MONTHLY));
    }
}
