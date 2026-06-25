package ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 DTO для получения статистики по длительности выполнения запросов к системе в целом
*/

@Getter
@Setter
@AllArgsConstructor
public class DurationStatDTO
{
    private LocalDateTime date;

    private Double minDuration;

    private Double avgDuration;

    private Double maxDuration;

    private Double avgPredictDuration;

    private boolean anomaly;
}
