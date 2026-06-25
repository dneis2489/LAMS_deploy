package ru.pstu.lamsv2.services.findAnomalyValuesService;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.DurationStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.methodInterface.FindAnomalyInterface;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.methodInterface.FindNormalRangeForAnomalyInterface;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.serviceInterface.findAnomalyMaxDurationServiceInterface;
import ru.pstu.lamsv2.interfaces.statisticIntefaces.StatGetDurationRepoInterface;

import java.util.List;
import java.time.LocalDateTime;

/**
    Сервис реализующий методы поиска аномальных значений в данных по общей длительности выполнения запросов системой. С агрегацией:
        1. По часам
        2. По дням
        3. По месяцам
*/

@Service
public class findAnomalyMaxDurationService implements findAnomalyMaxDurationServiceInterface
{

    private final FindNormalRangeForAnomalyInterface<DurationStatDTO> findNormalRangeForAnomalyInterface;
    private final FindAnomalyInterface<DurationStatDTO> findAnomalyInterface;

    private final StatGetDurationRepoInterface statGetDurationRepoInterface;

    public findAnomalyMaxDurationService(
            FindNormalRangeForAnomalyInterface<DurationStatDTO> findNormalRangeForAnomalyInterface,
            FindAnomalyInterface<DurationStatDTO> findAnomalyInterface,
            StatGetDurationRepoInterface statGetDurationRepoInterface
    )
    {
        this.findNormalRangeForAnomalyInterface = findNormalRangeForAnomalyInterface;
        this.findAnomalyInterface = findAnomalyInterface;
        this.statGetDurationRepoInterface = statGetDurationRepoInterface;
    }

    //Поиск аномальных значений у длительности выполнения запросов с градацией по часам
    @Override
    public List<DataFormatFromPredictDTO> findAnomalyMaxDurationWithHour(int hour)
    {
        List<DurationStatDTO> data = actualRows(statGetDurationRepoInterface.getDurationWithHour(hour));
        List<Double> range = findNormalRangeForAnomalyInterface.getNormalValueRange(
                data,
                DurationStatDTO::getMaxDuration
        );
        List<DataFormatFromPredictDTO> anomaly = findAnomalyInterface.findAnomaly(
                range,
                data,
                DurationStatDTO::getDate,
                DurationStatDTO::getMaxDuration
        );
        statGetDurationRepoInterface.updateTotalDurationHourAnomalies(
                dates(data),
                anomalyDates(anomaly)
        );
        return anomaly;
    }

    //Поиск аномальных значений у количества выполнения запросов с градацией по дням
    @Override
    public List<DataFormatFromPredictDTO> findAnomalyMaxDurationWithDay(int days)
    {
        List<DurationStatDTO> data = actualRows(statGetDurationRepoInterface.getDurationWithDay(days));
        List<Double> range = findNormalRangeForAnomalyInterface.getNormalValueRange(
                data,
                DurationStatDTO::getMaxDuration
        );
        List<DataFormatFromPredictDTO> anomaly = findAnomalyInterface.findAnomaly(
                range,
                data,
                DurationStatDTO::getDate,
                DurationStatDTO::getMaxDuration
        );
        statGetDurationRepoInterface.updateTotalDurationDayAnomalies(
                dates(data),
                anomalyDates(anomaly)
        );
        return anomaly;
    }

    //Поиск аномальных значений у количества выполнения запросов с градацией по месяцам
    @Override
    public List<DataFormatFromPredictDTO> findAnomalyMaxDurationWithMonth(int month)
    {
        List<DurationStatDTO> data = actualRows(statGetDurationRepoInterface.getDurationWithMonth(month));
        List<Double> range = findNormalRangeForAnomalyInterface.getNormalValueRange(
                data,
                DurationStatDTO::getMaxDuration
        );
        List<DataFormatFromPredictDTO> anomaly = findAnomalyInterface.findAnomaly(
                range,
                data,
                DurationStatDTO::getDate,
                DurationStatDTO::getMaxDuration
        );
        statGetDurationRepoInterface.updateTotalDurationMonthAnomalies(
                dates(data),
                anomalyDates(anomaly)
        );
        return anomaly;
    }

    private List<LocalDateTime> dates(List<DurationStatDTO> data)
    {
        return data.stream().map(DurationStatDTO::getDate).toList();
    }

    private List<LocalDateTime> anomalyDates(List<DataFormatFromPredictDTO> data)
    {
        return data.stream().map(DataFormatFromPredictDTO::getDate).toList();
    }

    private List<DurationStatDTO> actualRows(List<DurationStatDTO> data)
    {
        return data.stream()
                .filter(row -> row.getMaxDuration() != null)
                .toList();
    }
}
