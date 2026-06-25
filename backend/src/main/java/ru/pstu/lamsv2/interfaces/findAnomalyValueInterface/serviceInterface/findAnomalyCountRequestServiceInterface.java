package ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.serviceInterface;

import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;

import java.util.List;

/**
    Интерфейс для описания методов сервиса поиска аномальных значений в данных по общему количеству запросов к системе с агрегацией:
        1. По часам
        2. По дням
        3. По месяцам
*/

public interface findAnomalyCountRequestServiceInterface
{
    //С агрегацией по часам
    List<DataFormatFromPredictDTO> findAnomalyCountRequestWithHour(int hour);

    //С агрегацией по дням
    List<DataFormatFromPredictDTO> findAnomalyCountRequestWithDay(int days);

    //С агрегацией по месяцам
    List<DataFormatFromPredictDTO> findAnomalyCountRequestWithMonth(int month);
}
