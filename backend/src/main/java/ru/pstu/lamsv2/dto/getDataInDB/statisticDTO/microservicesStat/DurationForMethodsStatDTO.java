package ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 DTO для получения статистики по длительности ответов у методов микросервисов
*/

@Getter
@Setter
@AllArgsConstructor
public class DurationForMethodsStatDTO
{
    private LocalDateTime date;

    private String microserviceName;

    private String action;

    private Double minDuration;

    private Double avgDuration;

    private Double maxDuration;

    private Double avgPredictDuration;
}
