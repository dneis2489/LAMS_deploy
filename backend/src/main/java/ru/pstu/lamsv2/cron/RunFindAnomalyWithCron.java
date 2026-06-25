package ru.pstu.lamsv2.cron;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.enums.AggregationType;
import ru.pstu.lamsv2.enums.NotificationCategory;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.serviceInterface.findAnomalyCountRequestServiceInterface;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.serviceInterface.findAnomalyMaxDurationServiceInterface;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.serviceInterface.findAnomalyRequestStatusServiceInterface;
import ru.pstu.lamsv2.interfaces.notificationInterfaces.NotificationServiceInterface;
import ru.pstu.lamsv2.subMethods.methodsForEnums.LengthPeriodForAggregation;

import java.util.List;
import java.util.Map;

/**
    Компонент реализующий поиск аномальных значений в данных по расписанию. Данный компонент включает:
        1. Поиск аномальных значений по максимальной длительности выполнения запросов системой в целом с градацией по дням/часам/месяцам
        2. Поиск аномальных значений по количеству запросов к системе в целом с градацией по дням/часам/месяцам
        3. Поиск аномальных значений по количеству статусов ответов системой в целом с градацией по дням/часам/месяцам
*/

@Component
public class RunFindAnomalyWithCron
{
    private final findAnomalyMaxDurationServiceInterface findAnomalyMaxDurationService;
    private final findAnomalyCountRequestServiceInterface findAnomalyCountRequestServiceInterface;
    private final findAnomalyRequestStatusServiceInterface findAnomalyRequestStatusServiceInterface;
    private final NotificationServiceInterface notificationService;

    public RunFindAnomalyWithCron(
            findAnomalyMaxDurationServiceInterface findAnomalyMaxDurationService,
            findAnomalyCountRequestServiceInterface findAnomalyCountRequestServiceInterface,
            findAnomalyRequestStatusServiceInterface findAnomalyRequestStatusServiceInterface,
            NotificationServiceInterface notificationService
    )
    {
        this.findAnomalyMaxDurationService = findAnomalyMaxDurationService;
        this.findAnomalyCountRequestServiceInterface = findAnomalyCountRequestServiceInterface;
        this.findAnomalyRequestStatusServiceInterface = findAnomalyRequestStatusServiceInterface;
        this.notificationService = notificationService;
    }

    @Scheduled(
            cron = "0 4 * * * *",
            zone = "Europe/Moscow"
    )
    public void findAnomalyMaxDurationWithHour()
    {
        List<DataFormatFromPredictDTO> forecastList = findAnomalyMaxDurationService.findAnomalyMaxDurationWithHour(
                LengthPeriodForAggregation.getLength(AggregationType.HOURLY)
        );
        notifyAnomaly("максимальной длительности запросов за час", forecastList);
    }

    @Scheduled(
            cron = "0 4 0 * * *",
            zone = "Europe/Moscow"
    )
    public void findAnomalyMaxDurationWithDay()
    {
        List<DataFormatFromPredictDTO> forecastList = findAnomalyMaxDurationService.findAnomalyMaxDurationWithDay(
                LengthPeriodForAggregation.getLength(AggregationType.DAILY)
        );
        notifyAnomaly("максимальной длительности запросов за день", forecastList);
    }

    @Scheduled(
            cron = "0 4 0 1 * *",
            zone = "Europe/Moscow"
    )
    public void findAnomalyMaxDurationWithMonth()
    {
        List<DataFormatFromPredictDTO> forecastList = findAnomalyMaxDurationService.findAnomalyMaxDurationWithMonth(
                LengthPeriodForAggregation.getLength(AggregationType.MONTHLY)
        );
        notifyAnomaly("максимальной длительности запросов за месяц", forecastList);
    }

    @Scheduled(
            cron = "0 4 * * * *",
            zone = "Europe/Moscow"
    )
    public void findAnomalyCountRequestWithHour()
    {
        List<DataFormatFromPredictDTO> forecastList = findAnomalyCountRequestServiceInterface.findAnomalyCountRequestWithHour(
                LengthPeriodForAggregation.getLength(AggregationType.HOURLY)
        );
        notifyAnomaly("количества запросов за час", forecastList);
    }

    @Scheduled(
            cron = "0 4 0 * * *",
            zone = "Europe/Moscow"
    )
    public void findAnomalyCountRequestWithDay()
    {
        List<DataFormatFromPredictDTO> forecastList = findAnomalyCountRequestServiceInterface.findAnomalyCountRequestWithDay(
                LengthPeriodForAggregation.getLength(AggregationType.DAILY)
        );
        notifyAnomaly("количества запросов за день", forecastList);
    }

    @Scheduled(
            cron = "0 4 0 1 * *",
            zone = "Europe/Moscow"
    )
    public void findAnomalyCountRequestWithMonth()
    {
        List<DataFormatFromPredictDTO> forecastList = findAnomalyCountRequestServiceInterface.findAnomalyCountRequestWithMonth(
                LengthPeriodForAggregation.getLength(AggregationType.MONTHLY)
        );
        notifyAnomaly("количества запросов за месяц", forecastList);
    }

    @Scheduled(
            cron = "0 4 * * * *",
            zone = "Europe/Moscow"
    )
    public void findAnomalyRequestStatusWithHour()
    {
        Map<Integer, List<DataFormatFromPredictDTO>> forecastList = findAnomalyRequestStatusServiceInterface.findAnomalyRequestStatusWithHour(
                LengthPeriodForAggregation.getLength(AggregationType.HOURLY)
        );
        notifyStatusAnomaly("количества ошибок за час", forecastList);
    }

    @Scheduled(
            cron = "0 4 0 * * *",
            zone = "Europe/Moscow"
    )
    public void findAnomalyRequestStatusWithDay()
    {
        Map<Integer, List<DataFormatFromPredictDTO>> forecastList = findAnomalyRequestStatusServiceInterface.findAnomalyRequestStatusWithDay(
                LengthPeriodForAggregation.getLength(AggregationType.DAILY)
        );
        notifyStatusAnomaly("количества ошибок за день", forecastList);
    }

    @Scheduled(
            cron = "0 4 0 1 * *",
            zone = "Europe/Moscow"
    )
    public void findAnomalyRequestStatusWithMonth()
    {
        Map<Integer, List<DataFormatFromPredictDTO>> forecastList = findAnomalyRequestStatusServiceInterface.findAnomalyRequestStatusWithMonth(
                LengthPeriodForAggregation.getLength(AggregationType.MONTHLY)
        );
        notifyStatusAnomaly("количества ошибок за месяц", forecastList);
    }

    private void notifyAnomaly(String metricName, List<DataFormatFromPredictDTO> anomalyList)
    {
        if (anomalyList == null || anomalyList.isEmpty())
        {
            return;
        }

        DataFormatFromPredictDTO anomaly = anomalyList.get(anomalyList.size() - 1);
        notificationService.enqueue(
                NotificationCategory.ANOMALY_DETECTED,
                "LAMS: зафиксировано аномальное значение",
                "Обнаружена аномалия для метрики " + metricName + ".\n"
                        + "Дата: " + anomaly.getDate() + "\n"
                        + "Значение: " + anomaly.getData()
        );
    }

    private void notifyStatusAnomaly(String metricName, Map<Integer, List<DataFormatFromPredictDTO>> anomalyMap)
    {
        if (anomalyMap == null || anomalyMap.isEmpty())
        {
            return;
        }

        anomalyMap.forEach((statusCode, anomalyList) ->
                notifyAnomaly(metricName + " со статусом " + statusCode, anomalyList)
        );
    }
}
