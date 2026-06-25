package ru.pstu.lamsv2.dto.application.statisticDTO.countRequestForMethodsAggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
    DTO для сгруппированных по методам микросервисов данных о количестве запросов к системе
*/

@Getter
@Setter
@AllArgsConstructor
public class ActionListForCountRequestStatData
{
    private String action;
    private List<StatDataForCountRequestData> statData;
}
