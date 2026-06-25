package ru.pstu.lamsv2.dto.application.gantDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
    DTO для получения данных об активности пользователей. Данные сгруппированы по пользователям.
*/

@Getter
@Setter
@AllArgsConstructor
public class UserActivityGrouppingDTO
{
    private String userName;

    private long count;

    private List<UserActivityData> data;
}
