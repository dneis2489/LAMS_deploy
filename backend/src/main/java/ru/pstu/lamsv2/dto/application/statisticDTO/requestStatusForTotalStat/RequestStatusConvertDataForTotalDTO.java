package ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForTotalStat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
    DTO для данных о типах ответов запросов к системе в целом
*/

@Getter
@Setter
@AllArgsConstructor
public class RequestStatusConvertDataForTotalDTO
{
    private Integer statusCode;

    private List<CountStatusCodeForTotalData> countsStatusCodeList;
}
