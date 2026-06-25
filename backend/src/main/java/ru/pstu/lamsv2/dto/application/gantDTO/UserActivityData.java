package ru.pstu.lamsv2.dto.application.gantDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 DTO инофрмации по активности пользователей.
*/

@Getter
@Setter
@AllArgsConstructor
public class UserActivityData
{
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private double duration;

    private String microserviceName;

    private String actionName;
}
