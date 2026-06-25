package ru.pstu.lamsv2.subMethods.convertData.converForWeb;


import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.CountStatusRequestStatDTO;
import ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForTotalStat.CountStatusCodeForTotalData;
import ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForTotalStat.RequestStatusConvertDataForTotalDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
    Конвертация данных статистики по количеству статусов ответов у системы из CountStatusRequestStatDTO в RequestStatusConvertDataForTotalDTO.
        Необходим для реализации группировки по статусам ответов у системы в целом и возвращении сгруппированных данных фронт-энду
*/

public class ConvertCountRequestTotalStatData
{
    public static List<RequestStatusConvertDataForTotalDTO> convertCountRequestStats(List<CountStatusRequestStatDTO> data)
    {
        if (data == null || data.isEmpty())
        {
            return Collections.emptyList();
        }

        Map<Integer, List<CountStatusRequestStatDTO>> byStatusCode = data.stream()
                .collect(Collectors.groupingBy(CountStatusRequestStatDTO::getStatusCode));

        return byStatusCode.entrySet().stream()
                .map(entry -> {
                    Integer statusCode = entry.getKey();
                    List<CountStatusRequestStatDTO> serviceData = entry.getValue();

                    List<CountStatusCodeForTotalData> statData = serviceData.stream()
                            .map(stat -> new CountStatusCodeForTotalData(
                                    stat.getDate(),
                                    stat.getCount(),
                                    stat.getPredict(),
                                    stat.isAnomaly()
                            ))
                            .collect(Collectors.toList());
                    return  new RequestStatusConvertDataForTotalDTO(statusCode,  statData);
                })
                .collect(Collectors.toList());
    }
}
