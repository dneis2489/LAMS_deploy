package ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.serviceInterface;

import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;

import java.util.List;

/**
    Интерфейс для описания методов сервиса поиска аномальных значений в данных по общей длительности выполнения запросов системой. С агрегацией:
        1. По часам
        2. По дням
        3. По месяцам
*/

public interface findAnomalyMaxDurationServiceInterface
{
    //С агрегацией по часам
    List<DataFormatFromPredictDTO> findAnomalyMaxDurationWithHour(int hour);

    //С агрегацией по дням
    List<DataFormatFromPredictDTO> findAnomalyMaxDurationWithDay(int days);

    //С агрегацией по месяцам
    List<DataFormatFromPredictDTO> findAnomalyMaxDurationWithMonth(int month);
}
