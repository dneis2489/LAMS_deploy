package ru.pstu.lamsv2.dto.application.statisticDTO.durationForMethodsAggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
    DTO для сгруппированных по методам микросервисов данных о длительности выполнения запросов к системе
*/

@Getter
@Setter
@AllArgsConstructor
public class ActionListForDurationData
{
    private String action;
    private List<StatDataForDurationData> statData;
}
