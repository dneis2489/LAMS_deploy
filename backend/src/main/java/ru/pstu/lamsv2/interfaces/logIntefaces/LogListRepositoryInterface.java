package ru.pstu.lamsv2.interfaces.logIntefaces;

import ru.pstu.lamsv2.dto.getDataInDB.logDTO.LogListDTO;
import ru.pstu.lamsv2.dto.application.logListFilterDTO.FilterDTO;
import ru.pstu.lamsv2.dto.application.logListFilterDTO.SmartLogSearchDTO;

import java.util.List;

/**
    Интерфейс для описания методов репозитория получения данных для перечня логов. Включает в себя методы:
        1. Получения перечня логов с краткой информацией по каждому. С пагинацией.
        2. Получение перечня логов с краткой информацией по каждому. С пагинацией и фильтрацией.
*/

public interface LogListRepositoryInterface
{
    //Получение списка логов
    List<LogListDTO> getLogs(int page, int pageSize);

    //Получение отфильтрованного списка логов
    List<LogListDTO> getLogsByFilter(int page, int pageSize, FilterDTO filter);

    List<LogListDTO> getLogsBySmartSearch(int page, int pageSize, SmartLogSearchDTO search);
}
