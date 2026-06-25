package ru.pstu.lamsv2.services.logServices;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.getDataInDB.logDTO.FullLogInfoDTO;
import ru.pstu.lamsv2.dto.getDataInDB.logDTO.LogListDTO;
import ru.pstu.lamsv2.dto.application.logListFilterDTO.FilterDTO;
import ru.pstu.lamsv2.dto.application.logListFilterDTO.SmartLogSearchDTO;
import ru.pstu.lamsv2.interfaces.logIntefaces.LogInfoRepositoryInterface;
import ru.pstu.lamsv2.interfaces.logIntefaces.LogListRepositoryInterface;
import ru.pstu.lamsv2.interfaces.logIntefaces.LogServiceInterface;

import java.util.List;

/**
    Сервис реализующий методы получения данных для перечня логов. Включает в себя методы:
        1. Получения перечня логов с краткой информацией по каждому. С пагинацией.
        2. Получение перечня логов с краткой информацией по каждому. С пагинацией и фильтрацией.
        3. Получение полной информации по логу и связанному с ним логам.
*/

@Service
public class LogService implements LogServiceInterface
{
    private final LogListRepositoryInterface logListRepositoryInterface;
    private final LogInfoRepositoryInterface logInfoRepositoryInterface;

    public LogService(LogListRepositoryInterface logListRepositoryInterface, LogInfoRepositoryInterface logInfoRepositoryInterface)
    {
        this.logListRepositoryInterface = logListRepositoryInterface;
        this.logInfoRepositoryInterface = logInfoRepositoryInterface;
    }

    //Получение перечня логов
    @Override
    public List<LogListDTO> getLogs(int page, int pageSize)
    {
        return logListRepositoryInterface.getLogs(page, pageSize);
    }

    //Получение отфильтрованного перченя логов
    @Override
    public List<LogListDTO> getLogsByFilter(int page, int pageSize, FilterDTO filter)
    {
        return logListRepositoryInterface.getLogsByFilter(page, pageSize, filter);
    }

    @Override
    public List<LogListDTO> getLogsBySmartSearch(int page, int pageSize, SmartLogSearchDTO search)
    {
        return logListRepositoryInterface.getLogsBySmartSearch(page, pageSize, search);
    }

    //Получение инофрмации по конкретному логу
    @Override
    public List<FullLogInfoDTO> getLogInfo(long id)
    {
        return logInfoRepositoryInterface.getLogInfo(id);
    }
}
