package ru.pstu.lamsv2.subMethods.predictMethods;

import org.springframework.stereotype.Component;
import ru.pstu.lamsv2.config.prediction.HoltWintersConfig;
import ru.pstu.lamsv2.dto.application.predictDTO.DataFormatForPredictDTO;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.enums.AggregationType;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.PredictMethodInterface;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
    Компонент реализующий модель прогнозирования Хольта-Уинтерса. Включает в себя:
        1. Прием наименования модели для которой будет проводиться прогнозирования. Так как для каждой статистики нужна своя модель и свои коэффициенты
        2. Обучение модели прогнозирование
        3. Метод прогнозирования на предобученной модели
*/

@Component
//Модель прогнозирования Хольта-Уинтерса
public class HoltWintersPredict implements PredictMethodInterface
{
    private final HoltWintersConfig config;

    private final Map<String, ModelState> models = new HashMap<>();

    @Override
    public boolean modelExists(String modelName) {
        return models.containsKey(modelName);
    }

    public HoltWintersPredict(HoltWintersConfig config) {
        this.config = config;
    }

    private static class ModelState
    {
        double level;
        double trend;
        double[] seasonality;
        int seasonPeriod;
        int dataSize;
        String typeModel;
    }

    @Override
    public void train(
            String modelName,
            List<DataFormatForPredictDTO> data,
            int seasonPeriod,
            String typeModel
    )
    {
        if (data == null || seasonPeriod <= 0 || data.size() < seasonPeriod * 2)
        {
            return;
        }

        ModelState model = new ModelState();

        model.seasonPeriod = seasonPeriod;
        model.typeModel = typeModel;
        model.dataSize = data.size();

        double alpha = config.getAlpha();
        double beta = config.getBeta();
        double gamma = config.getGamma();

        model.level = java.util.stream.IntStream.range(0, seasonPeriod)
                .mapToDouble(i -> data.get(i).getData())
                .sum() / seasonPeriod;

        model.trend = java.util.stream.IntStream.range(0, seasonPeriod)
                .mapToDouble(i -> data.get(seasonPeriod + i).getData() - data.get(i).getData())
                .sum() / Math.pow(seasonPeriod, 2);

        model.seasonality = new double[seasonPeriod];

        if (typeModel.equals("add"))
        {
            for (int i = 0; i < seasonPeriod; i++) {
                model.seasonality[i] = data.get(i).getData() - model.level;
            }
        }
        else if (typeModel.equals("multi"))
        {
            for (int i = 0; i < seasonPeriod; i++)
            {
                model.seasonality[i] = data.get(i).getData() / model.level;
            }
        }

        for (int i = seasonPeriod; i < data.size(); i++)
        {
            double previousLevel = model.level;
            double S = model.seasonality[i % seasonPeriod];

            if (typeModel.equals("add"))
            {
                model.level = alpha * (data.get(i).getData() - S)
                        + (1 - alpha) * (previousLevel + model.trend);

                model.trend = beta * (model.level - previousLevel)
                        + (1 - beta) * model.trend;

                model.seasonality[i % seasonPeriod] =
                        gamma * (data.get(i).getData() - model.level)
                                + (1 - gamma) * S;

            }
            else if (typeModel.equals("multi"))
            {
                model.level = alpha * (data.get(i).getData() / S)
                        + (1 - alpha) * (previousLevel + model.trend);

                model.trend = beta * (model.level - previousLevel)
                        + (1 - beta) * model.trend;

                model.seasonality[i % seasonPeriod] =
                        gamma * (data.get(i).getData() / model.level)
                                + (1 - gamma) * S;
            }
        }

        models.put(modelName, model);
    }

    @Override
    public List<DataFormatFromPredictDTO> predict(
            String modelName,
            List<DataFormatForPredictDTO> data,
            int predictPeriod,
            AggregationType aggregationType
    )
    {
        ModelState model = models.get(modelName);


        if (model == null || data == null || data.isEmpty())
        {
            return List.of();
        }

        double[] result = new double[predictPeriod];

        for (int i = 0; i < predictPeriod; i++)
        {
            int seasonalIndex = (model.dataSize + i) % model.seasonPeriod;

            if (model.typeModel.equals("add"))
            {
                result[i] = model.level + (i + 1) * model.trend + model.seasonality[seasonalIndex];
            }
            else if (model.typeModel.equals("multi"))
            {
                result[i] = (model.level + (i + 1) * model.trend) * model.seasonality[seasonalIndex];
            }
        }

        LocalDateTime lastDate = data.get(data.size() - 1).getDate();
        List<DataFormatFromPredictDTO> forecastList = new ArrayList<>();

        for (int i = 0; i < predictPeriod; i++)
        {
            LocalDateTime forecastDate = switch (aggregationType)
            {
                case HOURLY -> lastDate.plusHours(i + 1);
                case DAILY -> lastDate.plusDays(i + 1);
                case MONTHLY -> lastDate.plusMonths(i + 1);
            };

            forecastList.add(new DataFormatFromPredictDTO(
                    forecastDate,
                    result[i]
            ));
        }

        return forecastList;
    }
}
