package ru.pstu.lamsv2.services.filterForClientService;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.getDataInDB.filterForClientDTO.RequestStatusListForFilterDTO;
import ru.pstu.lamsv2.dto.getDataInDB.filterForClientDTO.MicrosAndActionListForFilterDTO;
import ru.pstu.lamsv2.interfaces.FilterInterface.GetDataForFilterRepoInterface;
import ru.pstu.lamsv2.interfaces.FilterInterface.GetDataForFilterServiceInterface;

import java.util.List;

/**
    Сервис реализующий методы получения данных для фильтров. Содержит методы:
        1. Получение списка микросервисов и их методов
        2. Получение списка статусов ответов
*/

@Service
public class filterForClientService implements GetDataForFilterServiceInterface
{
    private final GetDataForFilterRepoInterface  getDataForFilterRepo;

    public filterForClientService(GetDataForFilterRepoInterface getDataForFilterRepo)
    {
        this.getDataForFilterRepo = getDataForFilterRepo;
    }

    //Получение списка микросервисов и их методов
    @Override
    public List<MicrosAndActionListForFilterDTO> getMicroserviceAndActionToFilter()
    {
        return getDataForFilterRepo.getMicroserviceAndActionToFilter();
    }

    //Получение списка статусов ответов
    @Override
    public List<RequestStatusListForFilterDTO> getRequestStatusToFilter()
    {
        return getDataForFilterRepo.getRequestStatusToFilter();
    }
}
