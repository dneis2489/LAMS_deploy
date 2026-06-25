package ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForMethodsAggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
    DTO для сгруппированных по методам микросервисов данных о типах ответов запросов к системе
*/

@Getter
@Setter
@AllArgsConstructor
public class RequestStatusConvertDataDTO
{
    private String microserviceName;
    private List<ActionListForStatusRequestData> actionList;
}
