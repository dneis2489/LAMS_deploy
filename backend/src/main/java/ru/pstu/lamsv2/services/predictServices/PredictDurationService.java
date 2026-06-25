package ru.pstu.lamsv2.services.predictServices;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.DurationStatDTO;
import ru.pstu.lamsv2.dto.application.predictDTO.DataFormatForPredictDTO;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.enums.AggregationType;
import ru.pstu.lamsv2.enums.TypePredictAccuracy;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.CheckPredictAccuracyInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.HoltWintersOptimizationInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.PredictMethodInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.serviceInterface.PredictDurationServiceInterface;
import ru.pstu.lamsv2.interfaces.statisticIntefaces.StatGetDurationRepoInterface;
import ru.pstu.lamsv2.subMethods.convertData.convertForPredict.ConvertDataToPredict;
import ru.pstu.lamsv2.subMethods.methodsForEnums.PredictReriodForPredict;
import ru.pstu.lamsv2.subMethods.methodsForEnums.SeasonPeriodForPredict;

import java.util.List;

/**
    Сервис реализующий методы обновления прогноза по длительности выполнения запросов. Включает в себя методы:
        1. Обновление таблицы данных с градацией по часам
        2. Обновление таблицы данных с градацией по дням
        3. Обновление таблицы данных с градацией по месяцам
*/

@Service
public class PredictDurationService implements PredictDurationServiceInterface
{
    private final StatGetDurationRepoInterface statGetDurationRepoInterface;
    private final PredictMethodInterface predictMethodInterface;
    private final ConvertDataToPredict convertDataToPredict;
    private final CheckPredictAccuracyInterface checkAccuracy;
    private final HoltWintersOptimizationInterface optimizer;

    public PredictDurationService(
            StatGetDurationRepoInterface statGetDurationRepoInterface,
            PredictMethodInterface predictMethodInterface,
            ConvertDataToPredict convertDataToPredict,
            CheckPredictAccuracyInterface checkAccuracy,
            HoltWintersOptimizationInterface optimizer
    )
    {
        this.statGetDurationRepoInterface = statGetDurationRepoInterface;
        this.predictMethodInterface = predictMethodInterface;
        this.convertDataToPredict = convertDataToPredict;
        this.checkAccuracy = checkAccuracy;
        this.optimizer = optimizer;
    }

    //Прогнозирование длительности выполнения запросов по часам
    @Override
    public List<DataFormatFromPredictDTO> predictDurationWithHour(int hour)
    {
        List<DataFormatForPredictDTO> data = convertDataToPredict.convertData(
                statGetDurationRepoInterface.getDurationWithHour(hour),
                DurationStatDTO::getDate,
                DurationStatDTO::getAvgDuration,
                DurationStatDTO::getAvgPredictDuration
        );

        if(!predictMethodInterface.modelExists("predictDurationWithHour"))
        {
            predictMethodInterface.train(
                    "predictDurationWithHour",
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.HOURLY),
                    "add"
            );
        }

        double error = checkAccuracy.checkAccuracy(data, TypePredictAccuracy.RMSE);

        if (error > 0.15)
        {
            optimizer.optimize(
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.HOURLY),
                    "add",
                    AggregationType.HOURLY,
                    TypePredictAccuracy.RMSE
            );

            predictMethodInterface.train(
                    "predictDurationWithHour",
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.HOURLY),
                    "add"
            );
        }

        return predictMethodInterface.predict(
                "predictDurationWithHour",
                data,
                PredictReriodForPredict.getPredictReriod(AggregationType.HOURLY),
                AggregationType.HOURLY
        );
    }

    //Прогнозирование длительности выполнения запросов по дням
    @Override
    public List<DataFormatFromPredictDTO> predictDurationWithDay(int days)
    {
        List<DataFormatForPredictDTO> data = convertDataToPredict.convertData(
                statGetDurationRepoInterface.getDurationWithDay(days),
                DurationStatDTO::getDate,
                DurationStatDTO::getAvgDuration,
                DurationStatDTO::getAvgPredictDuration
        );

        if(!predictMethodInterface.modelExists("predictDurationWithDay"))
        {
            predictMethodInterface.train(
                    "predictDurationWithDay",
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.DAILY),
                    "add"
            );
        }

        double error = checkAccuracy.checkAccuracy(data, TypePredictAccuracy.RMSE);

        if (error > 0.15)
        {
            optimizer.optimize(
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.DAILY),
                    "add",
                    AggregationType.DAILY,
                    TypePredictAccuracy.RMSE
            );

            predictMethodInterface.train(
                    "predictDurationWithDay",
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.DAILY),
                    "add"
            );
        }

        return predictMethodInterface.predict(
                "predictDurationWithDay",
                data,
                PredictReriodForPredict.getPredictReriod(AggregationType.DAILY),
                AggregationType.DAILY
        );
    }

    //Прогнозирование длительности выполнения запросов по месяцам
    @Override
    public List<DataFormatFromPredictDTO> predictDurationWithMonth(int month)
    {
        List<DataFormatForPredictDTO> data = convertDataToPredict.convertData(
                statGetDurationRepoInterface.getDurationWithMonth(month),
                DurationStatDTO::getDate,
                DurationStatDTO::getAvgDuration,
                DurationStatDTO::getAvgPredictDuration
        );

        if(!predictMethodInterface.modelExists("predictDurationWithMonth"))
        {
            predictMethodInterface.train(
                    "predictDurationWithMonth",
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.MONTHLY),
                    "add"
            );
        }

        double error = checkAccuracy.checkAccuracy(data, TypePredictAccuracy.RMSE);

        if (error > 0.15)
        {
            optimizer.optimize(
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.MONTHLY),
                    "add",
                    AggregationType.MONTHLY,
                    TypePredictAccuracy.RMSE
            );

            predictMethodInterface.train(
                    "predictDurationWithMonth",
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.MONTHLY),
                    "add"
            );
        }

        return predictMethodInterface.predict(
                "predictDurationWithMonth",
                data,
                PredictReriodForPredict.getPredictReriod(AggregationType.MONTHLY),
                AggregationType.MONTHLY
        );
    }
}
