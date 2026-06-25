package ru.pstu.lamsv2.dto.application.statisticDTO.durationForMethodsAggregation;

import lombok.*;

import java.time.LocalDateTime;

/**
    DTO для сгруппированных по методам микросервисов данных о длительности выполнения запросов к системе
*/

@Getter
@Setter
@AllArgsConstructor
public class StatDataForDurationData
{
    private LocalDateTime date;

    private Double minDuration;

    private Double avgDuration;

    private Double maxDuration;
}
