package ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 DTO для получения статистики по количеству статусов ответов у системы в целом
*/

@Getter
@Setter
@AllArgsConstructor
public class CountStatusRequestStatDTO
{
    private LocalDateTime date;

    private Integer statusCode;

    private Long count;

    private Double predict;

    private boolean anomaly;
}
