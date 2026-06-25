package ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
    DTO для получения статистики по количеству запросов к системе в целом
*/

@Getter
@Setter
@AllArgsConstructor
public class CountRequestStatDTO
{
    private LocalDateTime date;

    private Long count;

    private double predict;

    private boolean anomaly;
}
