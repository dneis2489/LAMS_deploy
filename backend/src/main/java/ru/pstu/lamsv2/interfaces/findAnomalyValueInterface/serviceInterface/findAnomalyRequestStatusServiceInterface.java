package ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.serviceInterface;

import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;

import java.util.List;
import java.util.Map;

/**
    Интерфейс для описания методов сервиса поиска аномальных значений в данных по общему количеству статусов ответов системой с агрегацией:
        1. По часам
        2. По дням
        3. По месяцам
*/

public interface findAnomalyRequestStatusServiceInterface
{
    //С агрегацией по часам
    Map<Integer, List<DataFormatFromPredictDTO>> findAnomalyRequestStatusWithHour(int hour);

    //С агрегацией по дням
    Map<Integer, List<DataFormatFromPredictDTO>> findAnomalyRequestStatusWithDay(int days);

    //С агрегацией по месяцам
    Map<Integer, List<DataFormatFromPredictDTO>> findAnomalyRequestStatusWithMonth(int month);
}
