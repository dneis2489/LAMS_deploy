package ru.pstu.lamsv2.dto.getDataInDB.filterForClientDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
    DTO для получения перечня статусов ответов из БД, для реализации фильтров на фронт-энде
*/

@Getter
@Setter
@AllArgsConstructor
public class RequestStatusListForFilterDTO
{
    private long id;

    private long name;
}
