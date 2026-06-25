package ru.pstu.lamsv2.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pstu.lamsv2.dto.getDataInDB.filterForClientDTO.MicrosAndActionListForFilterDTO;
import ru.pstu.lamsv2.dto.getDataInDB.filterForClientDTO.RequestStatusListForFilterDTO;
import ru.pstu.lamsv2.interfaces.FilterInterface.GetDataForFilterServiceInterface;

import java.util.List;

/**
    Контроллер получения данных для фильтров.
    Данный контроллер возвращает микросервисы и методы, а так же статусы запросов из БД,
  чтобы потом отобразить эти данные на фронте для выбора в фильтрах
*/

@RestController
@RequestMapping("lams")
public class GetDataForFilterController
{
    private final GetDataForFilterServiceInterface  getDataForFilterServiceInterface;

    public GetDataForFilterController(GetDataForFilterServiceInterface getDataForFilterServiceInterface)
    {
        this.getDataForFilterServiceInterface = getDataForFilterServiceInterface;
    }

    //Получение списка микросервисов и их методов
    @GetMapping("/getMicroserviceAndActionToFilter")
    public List<MicrosAndActionListForFilterDTO> getMicroserviceAndActionToFilter()
    {
        return  getDataForFilterServiceInterface.getMicroserviceAndActionToFilter();
    }

    //Получение списка микросервисов и их методов
    @GetMapping("/getRequestStatusToFilter")
    public List<RequestStatusListForFilterDTO> getRequestStatusToFilter()
    {
        return  getDataForFilterServiceInterface.getRequestStatusToFilter();
    }
}
