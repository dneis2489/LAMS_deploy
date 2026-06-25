package ru.pstu.lamsv2.subMethods.convertData.converForWeb;


import ru.pstu.lamsv2.dto.application.statisticDTO.countRequestForMethodsAggregation.ActionListForCountRequestStatData;
import ru.pstu.lamsv2.dto.application.statisticDTO.countRequestForMethodsAggregation.CountRequestConvertDataDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.countRequestForMethodsAggregation.StatDataForCountRequestData;
import ru.pstu.lamsv2.dto.application.statisticDTO.uniqueUsersForMethodsAggregation.ActionListForUniqueUsersData;
import ru.pstu.lamsv2.dto.application.statisticDTO.uniqueUsersForMethodsAggregation.StatDataForUniqueUsersData;
import ru.pstu.lamsv2.dto.application.statisticDTO.uniqueUsersForMethodsAggregation.UniqueUsersConvertDataDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.CountRequestForMethodsStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.UniqueUsersForMethodStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.UniqueUsersStatDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
    Конвертация данных статистики по уникальным пользователям из UniqueUsersForMethodStatDTO в UniqueUsersConvertDataDTO.
        Необходим для реализации группировки по методам микросервисов и возвращении сгруппированных данных фронт-энду
*/

public class ConvertUniqueUsersStatData
{
    public static List<UniqueUsersConvertDataDTO> convertUniqueUsersStat(List<UniqueUsersForMethodStatDTO> data)
    {
        if (data == null || data.isEmpty())
        {
            return Collections.emptyList();
        }

        Map<String, List<UniqueUsersForMethodStatDTO>> byMicroservice = data.stream()
                .collect(Collectors.groupingBy(UniqueUsersForMethodStatDTO::getMicroserviceName));

        return byMicroservice.entrySet().stream()
                .map(entry -> {
                    String microserviceName = entry.getKey();
                    List<UniqueUsersForMethodStatDTO> serviceLogs = entry.getValue();

                    // Группировка по action внутри каждого микросервиса
                    Map<String, List<UniqueUsersForMethodStatDTO>> byAction = serviceLogs.stream()
                            .collect(Collectors.groupingBy(UniqueUsersForMethodStatDTO::getActionName));

                    List<ActionListForUniqueUsersData> actionList = byAction.entrySet().stream()
                            .map(actionEntry -> {
                                String action = actionEntry.getKey();
                                List<UniqueUsersForMethodStatDTO> actionLogs = actionEntry.getValue();

                                // Преобразование в stat_data
                                List<StatDataForUniqueUsersData> statData = actionLogs.stream()
                                        .map(log -> new StatDataForUniqueUsersData(
                                                log.getDate(),
                                                log.getCount(),
                                                log.getUsers(),
                                                log.getPredict()
                                        ))
                                        .collect(Collectors.toList());

                                return new ActionListForUniqueUsersData(action, statData);
                            })
                            .collect(Collectors.toList());

                    return new UniqueUsersConvertDataDTO(microserviceName, actionList);
                })
                .collect(Collectors.toList());
    }
}
