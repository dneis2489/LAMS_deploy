package ru.pstu.lamsv2.dto.getDataInDB.filterForClientDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
     DTO для получения перечня микросервисов и их методов из БД, для реализации фильтров на фронт-энде
*/

@Getter
@Setter
@AllArgsConstructor
public class MicrosAndActionListForFilterDTO
{
    private long id;

    private long microId;

    private String microName;

    private String actionEng;

    private String actionRu;
}
