package ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
    DTO для получения статистики по количеству запросов к методам микросервисов
*/

@Getter
@Setter
@AllArgsConstructor
public class CountRequestForMethodsStatDTO
{
    private LocalDateTime date;

    private String microserviceName;

    private String action;

    private Long count;

    private double predict;
}
