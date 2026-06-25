package ru.pstu.lamsv2.subMethods.convertData.convertForPredict;

import org.springframework.stereotype.Component;
import ru.pstu.lamsv2.dto.application.predictDTO.DataFormatForPredictDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

/**
    Компонент для конвертации данных. Необходим, чтобы привести данные из разных статистик к общему формату перед отправкой их на прогнозирование.
*/

@Component
public class ConvertDataToPredict
{
    public <T> List<DataFormatForPredictDTO> convertData(
            List<T> data,
            Function<T, LocalDateTime> dateExtractor,
            Function<T, ? extends Number> valueExtractor,
            Function<T, Double> predictValueExtractor
    )
    {
        LocalDateTime now = LocalDateTime.now();

        return data.stream()
                .filter(src -> dateExtractor.apply(src).isBefore(now))
                .filter(src -> valueExtractor.apply(src) != null)
                .map(src -> new DataFormatForPredictDTO(
                        dateExtractor.apply(src),
                        valueExtractor.apply(src).doubleValue(),
                        predictValueExtractor.apply(src)
                ))
                .toList();
    }
}
