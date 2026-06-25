package ru.pstu.lamsv2.dto.application.statisticDTO.uniqueUsersForMethodsAggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
    DTO для сгруппированных по методам микросервисов данных о уникальных пользователях
*/

@Getter
@Setter
@AllArgsConstructor
public class ActionListForUniqueUsersData
{
    private String action;
    private List<StatDataForUniqueUsersData> statData;
}
