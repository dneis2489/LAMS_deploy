package ru.pstu.lamsv2.interfaces.predictInterface.methodInterface;

import ru.pstu.lamsv2.config.prediction.HoltWintersConfig;
import ru.pstu.lamsv2.dto.application.predictDTO.DataFormatForPredictDTO;
import ru.pstu.lamsv2.enums.AggregationType;
import ru.pstu.lamsv2.enums.TypePredictAccuracy;

import java.util.List;

/**
 Интерфейс для описания методов модели поиска оптимальных коэффициентов. Используется для поиска коэффициентов для модели Хольта-Уинтерса
*/

public interface HoltWintersOptimizationInterface
{
    HoltWintersConfig optimize(
            List<DataFormatForPredictDTO> data,
            int seasonPeriod,
            String typeModel,
            AggregationType aggregationType,
            TypePredictAccuracy accuracyType
    );
}
