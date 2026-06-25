package ru.pstu.lamsv2.cron;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.UniqueUserMethodForecastDTO;
import ru.pstu.lamsv2.enums.AggregationType;
import ru.pstu.lamsv2.enums.NotificationCategory;
import ru.pstu.lamsv2.interfaces.notificationInterfaces.NotificationServiceInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.repoInterface.PredictCountRequestRepoInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.repoInterface.PredictDurationRepoInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.repoInterface.PredictRequestStatusRepoInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.repoInterface.PredictUniqueUserRepoInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.serviceInterface.PredictCountRequestServiceInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.serviceInterface.PredictDurationServiceInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.serviceInterface.PredictRequestStatusServiceInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.serviceInterface.PredictUniqueUserServiceInterface;
import ru.pstu.lamsv2.services.predictServices.PredictCountRequestService;
import ru.pstu.lamsv2.subMethods.methodsForEnums.LengthPeriodForAggregation;
import ru.pstu.lamsv2.subMethods.methodsForEnums.LengthPeriodPredictForAggregation;

import java.util.List;
import java.util.Map;

/**
    Компонент реализующий запуск прогнозирования данных и обновления прогнозов в БД по расписанию. Данный компонент включает:
        1. Обновление прогноза по максимальной длительности выполнения запросов системой в целом с градацией по дням/часам/месяцам
        2. Обновление прогноза по количеству запросов к системе в целом с градацией по дням/часам/месяцам
        3. Обновление прогноза по количеству статусов ответов системой в целом с градацией по дням/часам/месяцам
        4. Обновление прогноза по количеству уникальных пользователей системы в целом с градацией по дням/часам/месяцам
*/

@Component
public class RunPredictWithCron
{
    private final PredictCountRequestServiceInterface predictCountRequestService;
    private final PredictCountRequestRepoInterface predictCountRequestRepoInterface;

    private final PredictRequestStatusServiceInterface predictRequestStatusService;
    private final PredictRequestStatusRepoInterface predictRequestStatusRepoInterface;

    private final PredictDurationServiceInterface predictDurationService;
    private final PredictDurationRepoInterface predictDurationRepoInterface;

    private final PredictUniqueUserServiceInterface  predictUniqueUserService;
    private final PredictUniqueUserRepoInterface predictUniqueUserRepoInterface;
    private final NotificationServiceInterface notificationService;
    private final double errorForecastThreshold;


    public RunPredictWithCron(PredictCountRequestService predictCountRequestService,
                              PredictCountRequestRepoInterface predictCountRequestRepoInterface,
                              PredictRequestStatusServiceInterface predictRequestStatusService,
                              PredictRequestStatusRepoInterface predictRequestStatusRepoInterface,
                              PredictDurationServiceInterface predictDurationService,
                              PredictDurationRepoInterface predictDurationRepoInterface,
                              PredictUniqueUserServiceInterface predictUniqueUserService,
                              PredictUniqueUserRepoInterface predictUniqueUserRepoInterface,
                              NotificationServiceInterface notificationService,
                              @Value("${lams.notifications.error-forecast-threshold:10}") double errorForecastThreshold
    )
    {
        this.predictCountRequestService = predictCountRequestService;
        this.predictCountRequestRepoInterface = predictCountRequestRepoInterface;
        this.predictRequestStatusService = predictRequestStatusService;
        this.predictRequestStatusRepoInterface = predictRequestStatusRepoInterface;
        this.predictDurationService = predictDurationService;
        this.predictDurationRepoInterface = predictDurationRepoInterface;
        this.predictUniqueUserService = predictUniqueUserService;
        this.predictUniqueUserRepoInterface = predictUniqueUserRepoInterface;
        this.notificationService = notificationService;
        this.errorForecastThreshold = errorForecastThreshold;
    }

    //Общее количество запросов к системе с агрегацией по часам
    @Scheduled(
            cron = "0 2 * * * *",
            zone = "Europe/Moscow"
    )
    public void updateTotalCountRequestWithHour()
    {
        List<DataFormatFromPredictDTO> forecastList = predictCountRequestService.predictCountRequestWithHour(
                LengthPeriodPredictForAggregation.getLength(AggregationType.HOURLY));
        predictCountRequestRepoInterface.updateTotalCountRequestWithHourInDB(forecastList);
    }

    //Общее количество запросов к системе с агрегацией по дням
    @Scheduled(
            cron = "0 2 0 * * *",
            zone = "Europe/Moscow"
    )
    public void updateTotalCountRequestWithDay()
    {
        List<DataFormatFromPredictDTO> forecastList = predictCountRequestService.predictCountRequestWithDay(
                LengthPeriodPredictForAggregation.getLength(AggregationType.DAILY));
        predictCountRequestRepoInterface.updateTotalCountRequestWithDayInDB(forecastList);
    }

    //Общее количество запросов к системе с агрегацией по месяцам
    @Scheduled(
            cron = "0 2 0 1 * *",
            zone = "Europe/Moscow"
    )
    public void updateTotalCountRequestWithMonth()
    {
        List<DataFormatFromPredictDTO> forecastList = predictCountRequestService.predictCountRequestWithMonth(
                LengthPeriodPredictForAggregation.getLength(AggregationType.MONTHLY));
        predictCountRequestRepoInterface.updateTotalCountRequestWithMonthInDB(forecastList);
    }

    //Общее количество статусов ответов системой с агрегацией по часам
    @Scheduled(
            cron = "0 2 * * * *",
            zone = "Europe/Moscow"
    )
    public void updateTotalStatusRequestCountWithHour()
    {
        Map<Integer, List<DataFormatFromPredictDTO>> forecastList = predictRequestStatusService.predictRequestStatusWithHour(
                LengthPeriodPredictForAggregation.getLength(AggregationType.HOURLY));
        predictRequestStatusRepoInterface.updateTotalRequestStatusWithHourInDB(forecastList);
        notifyHighErrorForecast(forecastList);
    }

    //Общее количество статусов ответов системой с агрегацией по дням
    @Scheduled(
            cron = "0 2 0 * * *",
            zone = "Europe/Moscow"
    )
    public void updateTotalStatusRequestCountWithDay()
    {
        Map<Integer, List<DataFormatFromPredictDTO>> forecastList = predictRequestStatusService.predictRequestStatusWithDay(
                LengthPeriodPredictForAggregation.getLength(AggregationType.DAILY));
        predictRequestStatusRepoInterface.updateTotalRequestStatusWithDayInDB(forecastList);
    }

    //Общее количество статусов ответов системой с агрегацией по месяцам
    @Scheduled(
            cron = "0 2 0 1 * *",
            zone = "Europe/Moscow"
    )
    public void updateTotalStatusRequestCountWithMonth()
    {
        Map<Integer, List<DataFormatFromPredictDTO>> forecastList = predictRequestStatusService.predictRequestStatusWithMonth(
                LengthPeriodPredictForAggregation.getLength(AggregationType.MONTHLY));
        predictRequestStatusRepoInterface.updateTotalRequestStatusWithMonthInDB(forecastList);
    }

    //Длительность выполнения запросов системой с агрегацией по часам
    @Scheduled(
            cron = "0 2 * * * *",
            zone = "Europe/Moscow"
    )
    public void updateTotalDurationWithHour()
    {
        List<DataFormatFromPredictDTO> forecastList = predictDurationService.predictDurationWithHour(
                LengthPeriodPredictForAggregation.getLength(AggregationType.HOURLY));
        predictDurationRepoInterface.updateTotalDurationWithHourInDB(forecastList);
    }

    //Длительность выполнения запросов системой с агрегацией по дням
    @Scheduled(
            cron = "0 2 0 * * *",
            zone = "Europe/Moscow"
    )
    public void updateTotalDurationWithDay()
    {
        List<DataFormatFromPredictDTO> forecastList = predictDurationService.predictDurationWithDay(
                LengthPeriodPredictForAggregation.getLength(AggregationType.DAILY));
        predictDurationRepoInterface.updateTotalDurationWithDayInDB(forecastList);
    }

    //Длительность выполнения запросов системой с агрегацией по месяцам
    @Scheduled(
            cron = "0 2 0 1 * *",
            zone = "Europe/Moscow"
    )
    public void updateTotalDurationWithMonth()
    {
        List<DataFormatFromPredictDTO> forecastList = predictDurationService.predictDurationWithMonth(
                LengthPeriodPredictForAggregation.getLength(AggregationType.MONTHLY));
        predictDurationRepoInterface.updateTotalDurationWithMonthInDB(forecastList);
    }

    //Количество уникальных пользователей в системе с агрегацией по часам
    @Scheduled(
            cron = "0 2 * * * *",
            zone = "Europe/Moscow"
    )
    public void updateUniqueUserWithHour()
    {
        int length = LengthPeriodPredictForAggregation.getLength(AggregationType.HOURLY);
        List<DataFormatFromPredictDTO> forecastList = predictUniqueUserService.predictGetUniqueUserWithHour(
                length);
        predictUniqueUserRepoInterface.updateUniqueUserWithHourInDB(forecastList);

        List<UniqueUserMethodForecastDTO> methodForecastList =
                predictUniqueUserService.predictGetUniqueUserForMethodsWithHour(length);
        predictUniqueUserRepoInterface.updateUniqueUserForMethodsWithHourInDB(methodForecastList);
    }

    //Количество уникальных пользователей в системе с агрегацией по дням
    @Scheduled(
            cron = "0 2 0 * * *",
            zone = "Europe/Moscow"
    )
    public void updateUniqueUserWithDay()
    {
        int length = LengthPeriodPredictForAggregation.getLength(AggregationType.DAILY);
        List<DataFormatFromPredictDTO> forecastList = predictUniqueUserService.predictGetUniqueUserWithDay(
                length);
        predictUniqueUserRepoInterface.updateUniqueUserWithDayInDB(forecastList);

        List<UniqueUserMethodForecastDTO> methodForecastList =
                predictUniqueUserService.predictGetUniqueUserForMethodsWithDay(length);
        predictUniqueUserRepoInterface.updateUniqueUserForMethodsWithDayInDB(methodForecastList);
    }

    //Количество уникальных пользователей в системе с агрегацией по месяцам
    @Scheduled(
            cron = "0 2 0 1 * *",
            zone = "Europe/Moscow"
    )
    public void updateUniqueUserWithMonth()
    {
        int length = LengthPeriodPredictForAggregation.getLength(AggregationType.MONTHLY);
        List<DataFormatFromPredictDTO> forecastList = predictUniqueUserService.predictGetUniqueUserWithMonth(
                length);
        predictUniqueUserRepoInterface.updateUniqueUserWithMonthInDB(forecastList);

        List<UniqueUserMethodForecastDTO> methodForecastList =
                predictUniqueUserService.predictGetUniqueUserForMethodsWithMonth(length);
        predictUniqueUserRepoInterface.updateUniqueUserForMethodsWithMonthInDB(methodForecastList);
    }

    private void notifyHighErrorForecast(Map<Integer, List<DataFormatFromPredictDTO>> forecastList)
    {
        if (forecastList == null || forecastList.isEmpty())
        {
            return;
        }

        forecastList.forEach((statusCode, predictedValues) -> {
            if (statusCode < 400 || predictedValues == null || predictedValues.isEmpty())
            {
                return;
            }

            DataFormatFromPredictDTO nextHourForecast = predictedValues.get(0);
            if (nextHourForecast.getData() < errorForecastThreshold)
            {
                return;
            }

            notificationService.enqueue(
                    NotificationCategory.ERROR_FORECAST,
                    "LAMS: прогнозируется большое количество ошибок",
                    "На следующий час спрогнозировано большое количество ошибок.\n"
                            + "HTTP-статус: " + statusCode + "\n"
                            + "Прогнозируемое количество: " + nextHourForecast.getData() + "\n"
                            + "Порог уведомления: " + errorForecastThreshold + "\n"
                            + "Время прогноза: " + nextHourForecast.getDate()
            );
        });
    }
}
