package ru.pstu.lamsv2.dto.application.statisticDTO.requestStatusForTotalStat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
    DTO для данных о типах ответов запросов к системе в целом
*/

@Getter
@Setter
@AllArgsConstructor
public class CountStatusCodeForTotalData
{
    private LocalDateTime date;

    private Long count;

    private Double predict;

    private boolean anomaly;
}
