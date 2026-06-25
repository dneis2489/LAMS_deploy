package ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.methodInterface;

import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
    Интерфейс для описания методов поиска аномальных значений.
    Значений не попадающих в интервал нормальных значений.
*/

public interface FindAnomalyInterface<T>
{
    List<DataFormatFromPredictDTO> findAnomaly (
            List<Double> range,
            List<T> data,
            Function<T, LocalDateTime> dateExtractor,
            ToDoubleFunction<T> valueExtractor
    );
}
