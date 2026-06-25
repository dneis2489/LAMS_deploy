package ru.pstu.lamsv2.subMethods.convertData.converForWeb;

import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.DurationForMethodsStatDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.durationForMethodsAggregation.ActionListForDurationData;
import ru.pstu.lamsv2.dto.application.statisticDTO.durationForMethodsAggregation.DurationConvertDataDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.durationForMethodsAggregation.StatDataForDurationData;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
    Конвертация данных статистики по длительности выполнения запросов системой из DurationForMethodsStatDTO в DurationConvertDataDTO.
        Необходим для реализации группировки по методам микросервисов и возвращении сгруппированных данных фронт-энду
*/

public class ConvertDurationStatData
{
    public static List<DurationConvertDataDTO> convertDurationStats(List<DurationForMethodsStatDTO> data)
    {
        if (data == null || data.isEmpty())
        {
            return Collections.emptyList();
        }

        Map<String, List<DurationForMethodsStatDTO>> byMicroservice = data.stream()
                .collect(Collectors.groupingBy(DurationForMethodsStatDTO::getMicroserviceName));

        return byMicroservice.entrySet().stream()
                .map(entry -> {
                    String microserviceName = entry.getKey();
                    List<DurationForMethodsStatDTO> serviceLogs = entry.getValue();

                    // Группировка по action внутри каждого микросервиса
                    Map<String, List<DurationForMethodsStatDTO>> byAction = serviceLogs.stream()
                            .collect(Collectors.groupingBy(DurationForMethodsStatDTO::getAction));

                    List<ActionListForDurationData> actionList = byAction.entrySet().stream()
                            .map(actionEntry -> {
                                String action = actionEntry.getKey();
                                List<DurationForMethodsStatDTO> actionLogs = actionEntry.getValue();

                                // Преобразование в stat_data
                                List<StatDataForDurationData> statData = actionLogs.stream()
                                        .map(log -> new StatDataForDurationData(
                                                log.getDate(),
                                                log.getMinDuration(),
                                                log.getAvgDuration(),
                                                log.getMaxDuration()
                                        ))
                                        .collect(Collectors.toList());

                                return new ActionListForDurationData(action, statData);
                            })
                            .collect(Collectors.toList());

                    return new DurationConvertDataDTO(microserviceName, actionList);
                })
                .collect(Collectors.toList());
    }
}
