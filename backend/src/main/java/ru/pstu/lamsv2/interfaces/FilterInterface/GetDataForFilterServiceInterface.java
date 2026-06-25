package ru.pstu.lamsv2.interfaces.FilterInterface;

import ru.pstu.lamsv2.dto.getDataInDB.filterForClientDTO.RequestStatusListForFilterDTO;
import ru.pstu.lamsv2.dto.getDataInDB.filterForClientDTO.MicrosAndActionListForFilterDTO;

import java.util.List;

/**
    Интерфейс для описания методов сервиса получения данных для фильтров. Содержит методы:
        1. Получение списка микросервисов и их методов
        2. Получение списка статусов ответов
*/

public interface GetDataForFilterServiceInterface
{
    //Получить список микросервисов и методов
    List<MicrosAndActionListForFilterDTO> getMicroserviceAndActionToFilter();

    //Получить список статусов ответов
    List<RequestStatusListForFilterDTO> getRequestStatusToFilter();
}
