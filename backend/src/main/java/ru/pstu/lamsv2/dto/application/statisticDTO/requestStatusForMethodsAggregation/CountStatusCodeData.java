package ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForMethodsAggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
    DTO для сгруппированных по методам микросервисов данных о типах ответов запросов к системе
*/

@Getter
@Setter
@AllArgsConstructor
public class CountStatusCodeData
{
    private LocalDateTime date;

    private Long count;

    private double predict;
}
