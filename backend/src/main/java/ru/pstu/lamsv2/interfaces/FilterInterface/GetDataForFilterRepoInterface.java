package ru.pstu.lamsv2.interfaces.FilterInterface;

import ru.pstu.lamsv2.dto.getDataInDB.filterForClientDTO.RequestStatusListForFilterDTO;
import ru.pstu.lamsv2.dto.getDataInDB.filterForClientDTO.MicrosAndActionListForFilterDTO;

import java.util.List;

/**
    Интерфейс для описания методов репозитория получения данных для фильтров. Содержит методы:
        1. Получить список микросервисов и их методов
        2. Получить список статусов ответов у логов
*/

public interface GetDataForFilterRepoInterface
{
    //Получить список микросервисов и методов
    List<MicrosAndActionListForFilterDTO> getMicroserviceAndActionToFilter();

    //Получить список статусов ответов
    List<RequestStatusListForFilterDTO> getRequestStatusToFilter();
}
