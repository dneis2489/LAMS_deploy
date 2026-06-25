package ru.pstu.lamsv2.interfaces.logIntefaces;

import ru.pstu.lamsv2.dto.getDataInDB.logDTO.FullLogInfoDTO;

import java.util.List;

/**
 Интерфейс для описания методов репозитория получения полной информации о логе и связанном с ним логами
*/

public interface LogInfoRepositoryInterface
{
    /**Получение полной инфо по логу */
    List<FullLogInfoDTO> getLogInfo(long id);
}
