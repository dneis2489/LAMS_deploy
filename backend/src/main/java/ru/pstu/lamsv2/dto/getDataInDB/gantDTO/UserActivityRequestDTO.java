package ru.pstu.lamsv2.dto.getDataInDB.gantDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
    DTO для получения данных об активности пользователей, для реализации диаграммы Ганта на фронт-энде
*/

@Getter
@Setter
@AllArgsConstructor
public class UserActivityRequestDTO
{
    private String userName;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private double duration;

    private String microserviceName;

    private String actionName;
}
