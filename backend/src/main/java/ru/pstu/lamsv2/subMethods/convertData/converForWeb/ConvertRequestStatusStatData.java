package ru.pstu.lamsv2.subMethods.convertData.converForWeb;


import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.CountStatusRequestForMethodsStatDTO;

import ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForMethodsAggregation.ActionListForStatusRequestData;
import ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForMethodsAggregation.RequestStatusConvertDataDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForMethodsAggregation.CodeListDataForStatusRequestData;
import ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForMethodsAggregation.CountStatusCodeData;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
    Конвертация данных статистики по количеству статусов ответов у системы из CountStatusRequestForMethodsStatDTO в RequestStatusConvertDataDTO.
        Необходим для реализации группировки по методам микросервисов и возвращении сгруппированных данных фронт-энду
*/

public class ConvertRequestStatusStatData
{
    public static List<RequestStatusConvertDataDTO> convertRequestStatusStats(List<CountStatusRequestForMethodsStatDTO> data)
    {
        if (data == null || data.isEmpty())
        {
            return Collections.emptyList();
        }

        Map<String, List<CountStatusRequestForMethodsStatDTO>> byMicroservice = data.stream()
                .collect(Collectors.groupingBy(CountStatusRequestForMethodsStatDTO::getMicroserviceName));

        return byMicroservice.entrySet().stream()
                .map(entry -> {
                    String microserviceName = entry.getKey();
                    List<CountStatusRequestForMethodsStatDTO> serviceLogs = entry.getValue();

                    // Группировка по action внутри каждого микросервиса
                    Map<String, List<CountStatusRequestForMethodsStatDTO>> byAction = serviceLogs.stream()
                            .collect(Collectors.groupingBy(CountStatusRequestForMethodsStatDTO::getAction));

                    List<ActionListForStatusRequestData> actionList = byAction.entrySet().stream()
                            .map(actionEntry -> {
                                String action = actionEntry.getKey();
                                List<CountStatusRequestForMethodsStatDTO> actionLogs = actionEntry.getValue();

                                Map<Integer, List<CountStatusRequestForMethodsStatDTO>> byCode = actionLogs.stream()
                                        .collect(Collectors.groupingBy(CountStatusRequestForMethodsStatDTO::getStatusCode));

                                List<CodeListDataForStatusRequestData> codeList = byCode.entrySet().stream()
                                        .map(codeEntry -> {
                                            int code = codeEntry.getKey();
                                            List<CountStatusRequestForMethodsStatDTO> codeLogs = codeEntry.getValue();

                                            // Преобразование в stat_data
                                            List<CountStatusCodeData> statData = codeLogs.stream()
                                                    .map(log -> new CountStatusCodeData(
                                                            log.getDate(),
                                                            log.getCount(),
                                                            log.getPredict()
                                                    ))
                                                    .collect(Collectors.toList());

                                            return new CodeListDataForStatusRequestData(code, statData);
                                        })
                                        .collect(Collectors.toList());
                                return new ActionListForStatusRequestData(action, codeList);
                            })
                            .collect(Collectors.toList());
                    return new RequestStatusConvertDataDTO(microserviceName, actionList);
                })
                .collect(Collectors.toList());
    }
}
