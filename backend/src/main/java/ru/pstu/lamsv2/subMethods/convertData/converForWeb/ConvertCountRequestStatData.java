package ru.pstu.lamsv2.subMethods.convertData.converForWeb;


import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.CountRequestForMethodsStatDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.countRequestForMethodsAggregation.ActionListForCountRequestStatData;
import ru.pstu.lamsv2.dto.application.statisticDTO.countRequestForMethodsAggregation.CountRequestConvertDataDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.countRequestForMethodsAggregation.StatDataForCountRequestData;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
    Конвертация данных статистики по количеству запросов к системе из CountRequestForMethodsStatDTO в CountRequestConvertDataDTO.
        Необходим для реализации группировки по методам микросервисов и возвращении сгруппированных данных фронт-энду
*/

public class ConvertCountRequestStatData
{
    public static List<CountRequestConvertDataDTO> convertCountRequestStats(List<CountRequestForMethodsStatDTO> data)
    {
        if (data == null || data.isEmpty())
        {
            return Collections.emptyList();
        }

        Map<String, List<CountRequestForMethodsStatDTO>> byMicroservice = data.stream()
                .collect(Collectors.groupingBy(CountRequestForMethodsStatDTO::getMicroserviceName));

        return byMicroservice.entrySet().stream()
                .map(entry -> {
                    String microserviceName = entry.getKey();
                    List<CountRequestForMethodsStatDTO> serviceLogs = entry.getValue();

                    // Группировка по action внутри каждого микросервиса
                    Map<String, List<CountRequestForMethodsStatDTO>> byAction = serviceLogs.stream()
                            .collect(Collectors.groupingBy(CountRequestForMethodsStatDTO::getAction));

                    List<ActionListForCountRequestStatData> actionList = byAction.entrySet().stream()
                            .map(actionEntry -> {
                                String action = actionEntry.getKey();
                                List<CountRequestForMethodsStatDTO> actionLogs = actionEntry.getValue();

                                // Преобразование в stat_data
                                List<StatDataForCountRequestData> statData = actionLogs.stream()
                                        .map(log -> new StatDataForCountRequestData(
                                                log.getDate(),
                                                log.getCount()
                                        ))
                                        .collect(Collectors.toList());

                                return new ActionListForCountRequestStatData(action, statData);
                            })
                            .collect(Collectors.toList());

                    return new CountRequestConvertDataDTO(microserviceName, actionList);
                })
                .collect(Collectors.toList());
    }
}
