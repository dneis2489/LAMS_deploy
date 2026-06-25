package ru.pstu.lamsv2.services.predictServices;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.UniqueUsersStatDTO;
import ru.pstu.lamsv2.dto.application.predictDTO.DataFormatForPredictDTO;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.enums.AggregationType;
import ru.pstu.lamsv2.enums.TypePredictAccuracy;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.CheckPredictAccuracyInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.HoltWintersOptimizationInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.PredictMethodInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.serviceInterface.PredictUniqueUserServiceInterface;
import ru.pstu.lamsv2.interfaces.statisticIntefaces.StatGetUniqueUserRepoInterface;
import ru.pstu.lamsv2.subMethods.convertData.convertForPredict.ConvertDataToPredict;
import ru.pstu.lamsv2.subMethods.methodsForEnums.PredictReriodForPredict;
import ru.pstu.lamsv2.subMethods.methodsForEnums.SeasonPeriodForPredict;

import java.util.List;

/**
    Сервис для реализации методов обновления прогноза по количеству уникальных пользователей системы. Включает в себя методы:
        1. Обновление таблицы данных с градацией по часам
        2. Обновление таблицы данных с градацией по дням
        3. Обновление таблицы данных с градацией по месяцам
*/

@Service
public class PredictUniqueUserService implements PredictUniqueUserServiceInterface
{
    private final StatGetUniqueUserRepoInterface statGetUniqueUserRepository;
    private final PredictMethodInterface predictMethodInterface;
    private final ConvertDataToPredict convertDataToPredict;
    private final CheckPredictAccuracyInterface checkAccuracy;
    private final HoltWintersOptimizationInterface optimizer;

    public PredictUniqueUserService(
            StatGetUniqueUserRepoInterface statGetUniqueUserRepository,
            PredictMethodInterface predictMethodInterface,
            ConvertDataToPredict convertDataToPredict,
            CheckPredictAccuracyInterface checkAccuracy,
            HoltWintersOptimizationInterface optimizer
    )
    {
        this.statGetUniqueUserRepository = statGetUniqueUserRepository;
        this.predictMethodInterface = predictMethodInterface;
        this.convertDataToPredict = convertDataToPredict;
        this.checkAccuracy = checkAccuracy;
        this.optimizer = optimizer;
    }

    //Прогнозирование количества уникальных пользователей с градацией по часам
    @Override
    public List<DataFormatFromPredictDTO> predictGetUniqueUserWithHour(int hour)
    {
        List<DataFormatForPredictDTO> data = convertDataToPredict.convertData(
                statGetUniqueUserRepository.getUniqueUserWithHour(hour),
                UniqueUsersStatDTO::getDate,
                UniqueUsersStatDTO::getCount,
                UniqueUsersStatDTO::getPredict
        );

        if(!predictMethodInterface.modelExists("predictGetUniqueUserWithHour"))
        {
            predictMethodInterface.train(
                    "predictGetUniqueUserWithHour",
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
                    "predictGetUniqueUserWithHour",
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.HOURLY),
                    "add"
            );
        }

        return predictMethodInterface.predict(
                "predictGetUniqueUserWithHour",
                data,
                PredictReriodForPredict.getPredictReriod(AggregationType.HOURLY),
                AggregationType.HOURLY
        );
    }

    //Прогнозирование количества уникальных пользователей с градацией по дням
    @Override
    public List<DataFormatFromPredictDTO> predictGetUniqueUserWithDay(int days)
    {
        List<DataFormatForPredictDTO> data = convertDataToPredict.convertData(
                statGetUniqueUserRepository.getUniqueUserWithDay(days),
                UniqueUsersStatDTO::getDate,
                UniqueUsersStatDTO::getCount,
                UniqueUsersStatDTO::getPredict
        );

        if(!predictMethodInterface.modelExists("predictGetUniqueUserWithDay"))
        {
            predictMethodInterface.train(
                    "predictGetUniqueUserWithDay",
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
                    "predictGetUniqueUserWithDay",
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.DAILY),
                    "add"
            );
        }

        return predictMethodInterface.predict(
                "predictGetUniqueUserWithDay",
                data,
                PredictReriodForPredict.getPredictReriod(AggregationType.DAILY),
                AggregationType.DAILY
        );
    }

    //Прогнозирование количества уникальных пользователей с градацией по месяцам
    @Override
    public List<DataFormatFromPredictDTO> predictGetUniqueUserWithMonth(int month)
    {
        List<DataFormatForPredictDTO> data = convertDataToPredict.convertData(
                statGetUniqueUserRepository.getUniqueUserWithMonth(month),
                UniqueUsersStatDTO::getDate,
                UniqueUsersStatDTO::getCount,
                UniqueUsersStatDTO::getPredict
        );

        if(!predictMethodInterface.modelExists("predictGetUniqueUserWithMonth"))
        {
            predictMethodInterface.train(
                    "predictGetUniqueUserWithMonth",
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
                    "predictGetUniqueUserWithMonth",
                    data,
                    SeasonPeriodForPredict.getSeasonPeriod(AggregationType.MONTHLY),
                    "add"
            );
        }

        return predictMethodInterface.predict(
                "predictGetUniqueUserWithMonth",
                data,
                PredictReriodForPredict.getPredictReriod(AggregationType.MONTHLY),
                AggregationType.MONTHLY
        );
    }
}
