package ru.pstu.lamsv2.controllers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pstu.lamsv2.dto.getDataInDB.logDTO.FullLogInfoDTO;
import ru.pstu.lamsv2.dto.getDataInDB.logDTO.LogListDTO;
import ru.pstu.lamsv2.dto.application.logListFilterDTO.FilterDTO;
import ru.pstu.lamsv2.interfaces.logIntefaces.LogServiceInterface;
import ru.pstu.lamsv2.services.logServices.SmartLogSearchService;

import java.time.LocalDate;
import java.util.List;

/**
 Контроллер получения логов. Данный контроллер реализует следующие эндпоинты:
    1. Получение общего перечня логов с пагинацией и сортировкой от новых к старым
    2. Получение отфильтрованного перечня логов с пагинацией и сортировкой от новых к старым
    3. Получение полной информации по логу и связанному с ним лоов
*/

@RestController
@RequestMapping("lams")
public class LogController
{
    private final LogServiceInterface logListServiceInterface;
    private final SmartLogSearchService smartLogSearchService;

    public LogController(
                LogServiceInterface logListServiceInterface,
                SmartLogSearchService smartLogSearchService
    )
    {
        this.logListServiceInterface = logListServiceInterface;
        this.smartLogSearchService = smartLogSearchService;
    }

    //Получение списка логов
    @GetMapping("/loglist")
    public List<LogListDTO> getLogList(
            @Min(1)
            @RequestParam(defaultValue = "1") int p,
            @Min(1)
            @Max(50)
            @RequestParam (defaultValue = "15") int s
    )
    {
        return  logListServiceInterface.getLogs(p, s);
    }

    //Получение отфильтрованного списка логов
    @GetMapping("/loglist/filter")
    public List<LogListDTO> getLogListFilter(
            @Min(1)
            @RequestParam(defaultValue = "1") int p,
            @Min(1)
            @Max(50)
            @RequestParam (defaultValue = "15") int s,
            @RequestParam (required = false) List<Integer> micros,
            @RequestParam (required = false) List<Integer> action,
            @RequestParam (required = false) List<Integer> requestStatus,
            @RequestParam (required = false) LocalDate startDate,
            @RequestParam (required = false) LocalDate endDate,
            @RequestParam (required = false) Boolean withoutResponse
    )
    {
        FilterDTO filter = new FilterDTO();
        filter.setMicroservice(micros);
        filter.setAction(action);
        filter.setRequestStatus(requestStatus);
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);
        filter.setWithoutResponse(withoutResponse);
        return  logListServiceInterface.getLogsByFilter(p, s, filter);
    }

    @GetMapping("/loglist/smart-search")
    public List<LogListDTO> getLogListSmartSearch(
            @Min(1)
            @RequestParam(defaultValue = "1") int p,
            @Min(1)
            @Max(50)
            @RequestParam (defaultValue = "15") int s,
            @RequestParam String q
    )
    {
        return logListServiceInterface.getLogsBySmartSearch(p, s, smartLogSearchService.parse(q));
    }

    //Получение полной инфо по логу
    @GetMapping("/loginfo")
    public List<FullLogInfoDTO> getLog(
            @RequestParam long id
    )
    {
        return logListServiceInterface.getLogInfo(id);
    }
}
