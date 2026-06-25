package ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
    DTO для получения перечня уникальных пользователей по методам микросервисов
*/

@Getter
@Setter
@AllArgsConstructor
public class UniqueUsersForMethodStatDTO
{
    private LocalDateTime date;

    private String microserviceName;

    private String actionName;

    private Long count;

    private List<String> users;

    private double predict;
}
