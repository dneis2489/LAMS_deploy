package ru.pstu.lamsv2.interfaces.predictInterface.methodInterface;

import ru.pstu.lamsv2.dto.application.predictDTO.DataFormatForPredictDTO;
import ru.pstu.lamsv2.enums.TypePredictAccuracy;

import java.util.List;

/**
    Интерфейс для описания методов проверки ошибки в прогнозируемых данных.
        Принимает на вход данные (фактические и спрогнозированнные) и метод проверки ошибки
*/

public interface CheckPredictAccuracyInterface
{
    double checkAccuracy(List<DataFormatForPredictDTO> data, TypePredictAccuracy type);
}
