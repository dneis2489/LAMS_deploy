package ru.pstu.lamsv2.subMethods.predictMethods;

import org.springframework.stereotype.Component;
import ru.pstu.lamsv2.dto.application.predictDTO.DataFormatForPredictDTO;
import ru.pstu.lamsv2.enums.TypePredictAccuracy;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.CheckPredictAccuracyInterface;

import java.util.List;

/**
    Компонент для реализации методов проверки ошибки в прогнозируемых данных.
        Принимает на вход данные (фактические и спрогнозированнные) и метод проверки ошибки
*/

@Component
public class CheckPredictAccuracy implements CheckPredictAccuracyInterface
{
    //Реализует проверку ошибки в зависимости от выбранного метода проверки
    @Override
    public double checkAccuracy(List<DataFormatForPredictDTO> data, TypePredictAccuracy type)
    {
        if(type == TypePredictAccuracy.MAE)
        {
            return MAE(data);
        }
        else if(type == TypePredictAccuracy.RMSE)
        {
            return RMSE(data);
        }
        return 0;
    }

    //Метод проверки ошибки RMSE
    public double RMSE(List<DataFormatForPredictDTO> data)
    {
        if (data.size() == 0) {return 0;}
        else
        {
            double rmse = Math.sqrt(
                    (1.0 / data.size()) * (
                            data.stream()
                                    .mapToDouble(value -> Math.pow(
                                            value.getData() - value.getPrediction(), 2)
                                    ).sum()
                    )
            );
            double avgValue = (data.stream().mapToDouble(DataFormatForPredictDTO::getData).sum()) / data.size();

            return rmse / avgValue;
        }
    }

    //Метод проверки ошибки MAE
    public double MAE(List<DataFormatForPredictDTO> data)
    {
        if (data.size() == 0) {return 0;}
        else
        {
            double mae = data.stream()
                    .mapToDouble(v -> Math.abs(v.getPrediction() - v.getData()))
                    .average().orElse(0);
            double avgValue = (data.stream().mapToDouble(DataFormatForPredictDTO::getData).sum()) / data.size();

            return mae / avgValue;
        }
    }
}
