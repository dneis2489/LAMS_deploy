package ru.pstu.lamsv2.services.findAnomalyValuesService;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.CountRequestStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.methodInterface.FindAnomalyInterface;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.methodInterface.FindNormalRangeForAnomalyInterface;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.serviceInterface.findAnomalyCountRequestServiceInterface;
import ru.pstu.lamsv2.interfaces.statisticIntefaces.StatGetCountRequestRepoInterface;

import java.util.List;
import java.time.LocalDateTime;

/**
    Сервис реализующий методы поиска аномальных значений в данных по общему количеству статусов ответов системой с агрегацией:
        1. По часам
        2. По дням
        3. По месяцам
*/

@Service
public class findAnomalyCountRequestService implements findAnomalyCountRequestServiceInterface
{

    private final FindNormalRangeForAnomalyInterface<CountRequestStatDTO> findNormalRangeForAnomalyInterface;
    private final FindAnomalyInterface<CountRequestStatDTO> findAnomalyInterface;

    private final StatGetCountRequestRepoInterface statisticGetCountRequestRepositoryInterface;

    public findAnomalyCountRequestService(FindNormalRangeForAnomalyInterface<CountRequestStatDTO> findNormalRangeForAnomalyInterface,
                                          FindAnomalyInterface<CountRequestStatDTO> findAnomalyInterface,
                                          StatGetCountRequestRepoInterface statisticGetCountRequestRepositoryInterface
    )
    {
        this.findNormalRangeForAnomalyInterface = findNormalRangeForAnomalyInterface;
        this.findAnomalyInterface = findAnomalyInterface;
        this.statisticGetCountRequestRepositoryInterface = statisticGetCountRequestRepositoryInterface;
    }

    //Поиск аномальных значений у количества выполнения запросов с градацией по часам
    @Override
    public List<DataFormatFromPredictDTO> findAnomalyCountRequestWithHour(int hour)
    {
        List<CountRequestStatDTO> data = actualRows(statisticGetCountRequestRepositoryInterface.getCountRequestWithHour(hour));
        List<Double> range = findNormalRangeForAnomalyInterface.getNormalValueRange(
                data,
                CountRequestStatDTO::getCount
        );
        List<DataFormatFromPredictDTO> anomaly = findAnomalyInterface.findAnomaly(
                range,
                data,
                CountRequestStatDTO::getDate,
                CountRequestStatDTO::getCount
                );
        statisticGetCountRequestRepositoryInterface.updateTotalCountRequestHourAnomalies(
                dates(data),
                anomalyDates(anomaly)
        );
        return anomaly;
    }

    //Поиск аномальных значений у количества выполнения запросов с градацией по дням
    @Override
    public List<DataFormatFromPredictDTO> findAnomalyCountRequestWithDay(int days) {
        List<CountRequestStatDTO> data = actualRows(statisticGetCountRequestRepositoryInterface.getCountRequestWithDay(days));
        List<Double> range = findNormalRangeForAnomalyInterface.getNormalValueRange(
                data,
                CountRequestStatDTO::getCount
        );
        List<DataFormatFromPredictDTO> anomaly = findAnomalyInterface.findAnomaly(
                range,
                data,
                CountRequestStatDTO::getDate,
                CountRequestStatDTO::getCount
        );
        statisticGetCountRequestRepositoryInterface.updateTotalCountRequestDayAnomalies(
                dates(data),
                anomalyDates(anomaly)
        );
        return anomaly;
    }

    //Поиск аномальных значений у количества выполнения запросов с градацией по месяцам
    @Override
    public List<DataFormatFromPredictDTO> findAnomalyCountRequestWithMonth(int month)
    {
        List<CountRequestStatDTO> data = actualRows(statisticGetCountRequestRepositoryInterface.getCountRequestWithMonth(month));
        List<Double> range = findNormalRangeForAnomalyInterface.getNormalValueRange(
                data,
                CountRequestStatDTO::getCount
        );
        List<DataFormatFromPredictDTO> anomaly = findAnomalyInterface.findAnomaly(
                range,
                data,
                CountRequestStatDTO::getDate,
                CountRequestStatDTO::getCount
        );
        statisticGetCountRequestRepositoryInterface.updateTotalCountRequestMonthAnomalies(
                dates(data),
                anomalyDates(anomaly)
        );
        return anomaly;
    }

    private List<LocalDateTime> dates(List<CountRequestStatDTO> data)
    {
        return data.stream().map(CountRequestStatDTO::getDate).toList();
    }

    private List<LocalDateTime> anomalyDates(List<DataFormatFromPredictDTO> data)
    {
        return data.stream().map(DataFormatFromPredictDTO::getDate).toList();
    }

    private List<CountRequestStatDTO> actualRows(List<CountRequestStatDTO> data)
    {
        return data.stream()
                .filter(row -> row.getCount() != null)
                .toList();
    }
}
