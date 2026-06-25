package ru.pstu.lamsv2.services.predictServices;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.CountRequestStatDTO;
import ru.pstu.lamsv2.dto.application.predictDTO.DataFormatForPredictDTO;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.enums.AggregationType;
import ru.pstu.lamsv2.enums.TypePredictAccuracy;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.CheckPredictAccuracyInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.HoltWintersOptimizationInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.PredictMethodInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.serviceInterface.PredictCountRequestServiceInterface;
import ru.pstu.lamsv2.interfaces.statisticIntefaces.StatGetCountRequestRepoInterface;
import ru.pstu.lamsv2.subMethods.convertData.convertForPredict.ConvertDataToPredict;
import ru.pstu.lamsv2.subMethods.methodsForEnums.PredictReriodForPredict;
import ru.pstu.lamsv2.subMethods.methodsForEnums.SeasonPeriodForPredict;

import java.util.List;

/**
    Сервис реализующий методы обновления прогноза по общему количеству запросов. Включает в себя методы:
        1. Обновление таблицы данных с градацией по часам
        2. Обновление таблицы данных с градацией по дням
        3. Обновление таблицы данных с градацией по месяцам
*/

@Service
public class PredictCountRequestService implements PredictCountRequestServiceInterface
{
    private final StatGetCountRequestRepoInterface statisticGetCountRequestRepositoryInterface;
    private final PredictMethodInterface predictMethodInterface;
    private final ConvertDataToPredict convertDataToPredict;
    private final CheckPredictAccuracyInterface checkAccuracy;
    private final HoltWintersOptimizationInterface optimizer;

    public PredictCountRequestService(
            StatGetCountRequestRepoInterface statisticGetCountRequestRepositoryInterface,
            PredictMethodInterface predictMethodInterface,
            ConvertDataToPredict convertDataToPredict,
            CheckPredictAccuracyInterface checkAccuracy,
            HoltWintersOptimizationInterface optimizer
    )
    {
        this.statisticGetCountRequestRepositoryInterface = statisticGetCountRequestRepositoryInterface;
        this.predictMethodInterface = predictMethodInterface;
        this.convertDataToPredict = convertDataToPredict;
        this.checkAccuracy = checkAccuracy;
        this.optimizer = optimizer;
    }

    //Прогнозирование общего количества запросов по часам
    @Override
    public List<DataFormatFromPredictDTO> predictCountRequestWithHour(int hour)
    {
        List<DataFormatForPredictDTO> data = convertDataToPredict.convertData(
                statisticGetCountRequestRepositoryInterface.getCountRequestWithHour(hour),
                CountRequestStatDTO::getDate,
                CountRequestStatDTO::getCount,
                CountRequestStatDTO::getPredict
        );

        if(!predictMethodInterface.modelExists("predictCountRequestWithHour"))
        {
            predictMethodInterface.train(
                    "predictCountRequestWithHour",
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
                    "predictCountRequestWithHour",
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.HOURLY),
                    "add"
            );
        }

        return predictMethodInterface.predict(
                "predictCountRequestWithHour",
                data,
                PredictReriodForPredict.getPredictReriod(AggregationType.HOURLY),
                AggregationType.HOURLY
        );
    }

    //Прогнозирование общего количества запросов по дням
    @Override
    public List<DataFormatFromPredictDTO> predictCountRequestWithDay(int days)
    {
        List<DataFormatForPredictDTO> data = convertDataToPredict.convertData(
                statisticGetCountRequestRepositoryInterface.getCountRequestWithDay(days),
                CountRequestStatDTO::getDate,
                CountRequestStatDTO::getCount,
                CountRequestStatDTO::getPredict
        );

        if(!predictMethodInterface.modelExists("predictCountRequestWithDay"))
        {
            predictMethodInterface.train(
                    "predictCountRequestWithDay",
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
                    "predictCountRequestWithDay",
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.DAILY),
                    "add"
            );
        }

        return predictMethodInterface.predict(
                "predictCountRequestWithDay",
                data,
                PredictReriodForPredict.getPredictReriod(AggregationType.DAILY),
                AggregationType.DAILY
        );
    }

    //Прогнозирование общего количества запросов по месяцам
    @Override
    public List<DataFormatFromPredictDTO> predictCountRequestWithMonth(int month)
    {
        List<DataFormatForPredictDTO> data = convertDataToPredict.convertData(
                statisticGetCountRequestRepositoryInterface.getCountRequestWithMonth(month),
                CountRequestStatDTO::getDate,
                CountRequestStatDTO::getCount,
                CountRequestStatDTO::getPredict
        );

        if(!predictMethodInterface.modelExists("predictCountRequestWithMonth"))
        {
            predictMethodInterface.train(
                    "predictCountRequestWithMonth",
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
                    "predictCountRequestWithMonth",
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.MONTHLY),
                    "add"
            );
        }

        return predictMethodInterface.predict(
                "predictCountRequestWithMonth",
                data,
                PredictReriodForPredict.getPredictReriod(AggregationType.MONTHLY),
                AggregationType.MONTHLY
        );
    }
}
