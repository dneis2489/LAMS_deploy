package ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 DTO для получения статистики по количеству уникальных пользователей системы в целом
*/

@Getter
@Setter
@AllArgsConstructor
public class UniqueUsersStatDTO
{
    private Long id;

    private LocalDateTime date;

    private Long count;

    private double predict;

    private List<String> users;
}
