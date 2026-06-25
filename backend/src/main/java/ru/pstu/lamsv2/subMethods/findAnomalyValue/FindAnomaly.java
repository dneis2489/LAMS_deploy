package ru.pstu.lamsv2.subMethods.findAnomalyValue;

import org.springframework.stereotype.Component;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.methodInterface.FindAnomalyInterface;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
    Компонент реализующий метод поиска аномальных значений.
        Значений не попадающих в интервал нормальных значений.
*/

@Component
public class FindAnomaly<T> implements FindAnomalyInterface<T>
{

    @Override
    public List<DataFormatFromPredictDTO> findAnomaly(
            List<Double> range,
            List<T> data,
            Function<T, LocalDateTime> dateExtractor,
            ToDoubleFunction<T> valueExtractor
    )
    {
        double lowerBound = range.get(0);
        double upperBound = range.get(1);

        return data.stream()
                .filter(item -> {
                    double value = valueExtractor.applyAsDouble(item);
                    return value < lowerBound || value > upperBound;
                })
                .map(item -> new DataFormatFromPredictDTO(
                        dateExtractor.apply(item),
                        valueExtractor.applyAsDouble(item)
                ))
                .toList();
    }
}
