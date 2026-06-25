package ru.pstu.lamsv2.interfaces.predictInterface.methodInterface;

import ru.pstu.lamsv2.dto.application.predictDTO.DataFormatForPredictDTO;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.enums.AggregationType;

import java.util.List;

/**
    Интерфейс для описания методов системы прогнозирования. Система прогнозирования должна включать в себя:
        1. Прием наименования модели для которой будет проводиться прогнозирования. Так как для каждой статистики нужна своя модель и свои коэффициенты
        2. Обучение модели прогнозирование
        3. Метод прогнозирования на предобученной модели
*/

//Интерфейс для модели прогнозирования
public interface PredictMethodInterface
{
    //Прием наименования модели
    boolean modelExists(String modelName);

    //Обучение
    void train(
            String modelName,
            List<DataFormatForPredictDTO> data,
            int seasonPeriod,
            String typeModel
    );

    //Прогнозирование
    List<DataFormatFromPredictDTO> predict(
            String modelName,
            List<DataFormatForPredictDTO> data,
            int predictPeriod,
            AggregationType aggregationType
    );
}
