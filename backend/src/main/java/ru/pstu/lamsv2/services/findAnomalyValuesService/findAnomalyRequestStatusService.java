package ru.pstu.lamsv2.services.findAnomalyValuesService;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.CountStatusRequestStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.methodInterface.FindAnomalyInterface;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.methodInterface.FindNormalRangeForAnomalyInterface;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.serviceInterface.findAnomalyRequestStatusServiceInterface;
import ru.pstu.lamsv2.interfaces.statisticIntefaces.StatGetCountRequestStatusRepoInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
    Сервис реализующий методы поиска аномальных значений в данных по общему количеству статусов ответов системой с агрегацией:
        1. По часам
        2. По дням
        3. По месяцам
*/

@Service
public class findAnomalyRequestStatusService implements findAnomalyRequestStatusServiceInterface
{
    private final FindNormalRangeForAnomalyInterface<CountStatusRequestStatDTO> findNormalRangeForAnomalyInterface;
    private final FindAnomalyInterface<CountStatusRequestStatDTO> findAnomalyInterface;

    private final StatGetCountRequestStatusRepoInterface statGetCountRequestStatusRepoInterface;

    public findAnomalyRequestStatusService(
            FindNormalRangeForAnomalyInterface<CountStatusRequestStatDTO> findNormalRangeForAnomalyInterface,
            FindAnomalyInterface<CountStatusRequestStatDTO> findAnomalyInterface,
            StatGetCountRequestStatusRepoInterface statGetCountRequestStatusRepoInterface
    )
    {
        this.findNormalRangeForAnomalyInterface = findNormalRangeForAnomalyInterface;
        this.findAnomalyInterface = findAnomalyInterface;
        this.statGetCountRequestStatusRepoInterface = statGetCountRequestStatusRepoInterface;
    }

    //Поиск аномальных значений у количества статусов ответов с градацией по часам
    @Override
    public Map<Integer, List<DataFormatFromPredictDTO>> findAnomalyRequestStatusWithHour(int hour)
    {
        List<CountStatusRequestStatDTO> data = actualRows(statGetCountRequestStatusRepoInterface.getCountRequestStatusWithHour(hour));
        Map<Integer, List<CountStatusRequestStatDTO>> groupedData = grouping(data);

        Map<Integer, List<DataFormatFromPredictDTO>> anomalyData = new HashMap<>();

        Map<Integer, List<DataFormatFromPredictDTO>> result = getIntegerListMap(groupedData, anomalyData);
        statGetCountRequestStatusRepoInterface.updateTotalRequestStatusHourAnomalies(
                data,
                anomalyRows(data, result)
        );
        return result;
    }

    //Поиск аномальных значений у количества статусов ответов с градацией по дням
    @Override
    public Map<Integer, List<DataFormatFromPredictDTO>> findAnomalyRequestStatusWithDay(int days)
    {
        List<CountStatusRequestStatDTO> data = actualRows(statGetCountRequestStatusRepoInterface.getCountRequestStatusWithDay(days));
        Map<Integer, List<CountStatusRequestStatDTO>> groupedData = grouping(data);

        Map<Integer, List<DataFormatFromPredictDTO>> anomalyData = new HashMap<>();

        Map<Integer, List<DataFormatFromPredictDTO>> result = getIntegerListMap(groupedData, anomalyData);
        statGetCountRequestStatusRepoInterface.updateTotalRequestStatusDayAnomalies(
                data,
                anomalyRows(data, result)
        );
        return result;
    }

    //Поиск аномальных значений у количества статусов ответов с градацией по месяцам
    @Override
    public Map<Integer, List<DataFormatFromPredictDTO>> findAnomalyRequestStatusWithMonth(int month)
    {
        List<CountStatusRequestStatDTO> data = actualRows(statGetCountRequestStatusRepoInterface.getCountRequestStatusMonth(month));
        Map<Integer, List<CountStatusRequestStatDTO>> groupedData = grouping(data);

        Map<Integer, List<DataFormatFromPredictDTO>> anomalyData = new HashMap<>();

        Map<Integer, List<DataFormatFromPredictDTO>> result = getIntegerListMap(groupedData, anomalyData);
        statGetCountRequestStatusRepoInterface.updateTotalRequestStatusMonthAnomalies(
                data,
                anomalyRows(data, result)
        );
        return result;
    }

    //Группировка по статусу ответа
    public Map<Integer, List<CountStatusRequestStatDTO>> grouping (List<CountStatusRequestStatDTO> data)
    {
        return data.stream().collect(Collectors.groupingBy(CountStatusRequestStatDTO::getStatusCode));
    }

    //Поиск аномальных значений
    private Map<Integer, List<DataFormatFromPredictDTO>> getIntegerListMap(Map<Integer, List<CountStatusRequestStatDTO>> groupedData, Map<Integer, List<DataFormatFromPredictDTO>> anomalyData) {
        groupedData.forEach((code, stat) -> {
            if (code >= 400)
            {
                List<Double> range = findNormalRangeForAnomalyInterface.getNormalValueRange(
                        stat,
                        CountStatusRequestStatDTO::getCount
                );

                List<DataFormatFromPredictDTO> anomaly = findAnomalyInterface.findAnomaly(
                        range,
                        stat,
                        CountStatusRequestStatDTO::getDate,
                        CountStatusRequestStatDTO::getCount
                );

                anomalyData.put(code, anomaly);
            }
        });
        return anomalyData;
    }

    private List<CountStatusRequestStatDTO> anomalyRows(
            List<CountStatusRequestStatDTO> data,
            Map<Integer, List<DataFormatFromPredictDTO>> anomalyData
    )
    {
        return data.stream()
                .filter(row -> anomalyData.getOrDefault(row.getStatusCode(), List.of()).stream()
                        .anyMatch(anomaly -> anomaly.getDate().equals(row.getDate())))
                .toList();
    }

    private List<CountStatusRequestStatDTO> actualRows(List<CountStatusRequestStatDTO> data)
    {
        return data.stream()
                .filter(row -> row.getCount() != null)
                .toList();
    }
}
