package ru.pstu.lamsv2.services.predictServices;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.CountStatusRequestStatDTO;
import ru.pstu.lamsv2.dto.application.predictDTO.DataFormatForPredictDTO;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.enums.AggregationType;
import ru.pstu.lamsv2.enums.TypePredictAccuracy;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.CheckPredictAccuracyInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.HoltWintersOptimizationInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.PredictMethodInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.serviceInterface.PredictRequestStatusServiceInterface;
import ru.pstu.lamsv2.interfaces.statisticIntefaces.StatGetCountRequestStatusRepoInterface;
import ru.pstu.lamsv2.subMethods.convertData.convertForPredict.ConvertDataToPredict;
import ru.pstu.lamsv2.subMethods.methodsForEnums.PredictReriodForPredict;
import ru.pstu.lamsv2.subMethods.methodsForEnums.SeasonPeriodForPredict;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
    Сервис реализующий методы обновления прогноза по общему количеству статусов запросов. Включает в себя методы:
        1. Обновление таблицы данных с градацией по часам
        2. Обновление таблицы данных с градацией по дням
        3. Обновление таблицы данных с градацией по месяцам
*/

@Service
public class PredictRequestStatusService implements PredictRequestStatusServiceInterface
{
    private final StatGetCountRequestStatusRepoInterface statGetCountRequestStatusRepoInterface;
    private final PredictMethodInterface predictMethodInterface;
    private final ConvertDataToPredict convertDataToPredict;
    private final CheckPredictAccuracyInterface checkAccuracy;
    private final HoltWintersOptimizationInterface optimizer;

    public PredictRequestStatusService(
            StatGetCountRequestStatusRepoInterface statGetCountRequestStatusRepoInterface, PredictMethodInterface predictMethodInterface,
            ConvertDataToPredict convertDataToPredict, CheckPredictAccuracyInterface checkAccuracy, HoltWintersOptimizationInterface optimizer
    )
    {
        this.statGetCountRequestStatusRepoInterface = statGetCountRequestStatusRepoInterface;
        this.predictMethodInterface = predictMethodInterface;
        this.convertDataToPredict = convertDataToPredict;
        this.checkAccuracy = checkAccuracy;
        this.optimizer = optimizer;
    }

    public Map<Integer, List<CountStatusRequestStatDTO>> grouping (List<CountStatusRequestStatDTO> data)
    {
        return data.stream().collect(Collectors.groupingBy(CountStatusRequestStatDTO::getStatusCode));
    }

    @Override
    public Map<Integer, List<DataFormatFromPredictDTO>> predictRequestStatusWithHour(int hour)
    {

        List<CountStatusRequestStatDTO> data = statGetCountRequestStatusRepoInterface.getCountRequestStatusWithHour(hour);
        Map<Integer, List<CountStatusRequestStatDTO>> groupingData = grouping(data);
        Map<Integer, List<DataFormatFromPredictDTO>> predictedData = new HashMap<>();

        groupingData.forEach((statusCode, statusData) -> {
            List<DataFormatForPredictDTO> convertedData = convertDataToPredict.convertData(
                    statusData,
                    CountStatusRequestStatDTO::getDate,
                    CountStatusRequestStatDTO::getCount,
                    CountStatusRequestStatDTO::getPredict
            );

            if(!predictMethodInterface.modelExists("predictRequestStatusWithHour"))
            {
                predictMethodInterface.train(
                        "predictRequestStatusWithHour",
                        convertedData,
                        SeasonPeriodForPredict.getSeasonPeriod(AggregationType.HOURLY),
                        "add"
                );
            }

            double error = checkAccuracy.checkAccuracy(convertedData, TypePredictAccuracy.RMSE);

            if (error > 0.15)
            {
                optimizer.optimize(
                        convertedData,
                        SeasonPeriodForPredict.getSeasonPeriod(AggregationType.HOURLY),
                        "add",
                        AggregationType.HOURLY,
                        TypePredictAccuracy.RMSE
                );

                predictMethodInterface.train(
                        "predictRequestStatusWithHour",
                        convertedData,
                        SeasonPeriodForPredict.getSeasonPeriod(AggregationType.HOURLY),
                        "add"
                );
            }

            predictedData.put(
                    statusCode,
                    predictMethodInterface.predict(
                            "predictRequestStatusWithHour",
                            convertedData,
                            PredictReriodForPredict.getPredictReriod(AggregationType.HOURLY),
                            AggregationType.HOURLY
                    )
                    );
        });

        return predictedData;
    }

    @Override
    public Map<Integer, List<DataFormatFromPredictDTO>> predictRequestStatusWithDay(int days)
    {
        List<CountStatusRequestStatDTO> data = statGetCountRequestStatusRepoInterface.getCountRequestStatusWithDay(days);
        Map<Integer, List<CountStatusRequestStatDTO>> groupingData = grouping(data);
        Map<Integer, List<DataFormatFromPredictDTO>> predictedData = new HashMap<>();

        groupingData.forEach((statusCode, statusData) -> {
            List<DataFormatForPredictDTO> convertedData = convertDataToPredict.convertData(
                    statusData,
                    CountStatusRequestStatDTO::getDate,
                    CountStatusRequestStatDTO::getCount,
                    CountStatusRequestStatDTO::getPredict
            );

            if(!predictMethodInterface.modelExists("predictRequestStatusWithDay"))
            {
                predictMethodInterface.train(
                        "predictRequestStatusWithDay",
                        convertedData,
                        SeasonPeriodForPredict.getSeasonPeriod(AggregationType.DAILY),
                        "add"
                );
            }

            double error = checkAccuracy.checkAccuracy(convertedData, TypePredictAccuracy.RMSE);

            if (error > 0.15)
            {
                optimizer.optimize(
                        convertedData,
                        SeasonPeriodForPredict.getSeasonPeriod(AggregationType.DAILY),
                        "add",
                        AggregationType.DAILY,
                        TypePredictAccuracy.RMSE
                );

                predictMethodInterface.train(
                        "predictRequestStatusWithDay",
                        convertedData,
                        SeasonPeriodForPredict.getSeasonPeriod(AggregationType.DAILY),
                        "add"
                );
            }

            predictedData.put(
                    statusCode,
                    predictMethodInterface.predict(
                            "predictRequestStatusWithDay",
                            convertedData,
                            PredictReriodForPredict.getPredictReriod(AggregationType.DAILY),
                            AggregationType.DAILY
                    )
            );
        });

        return predictedData;
    }

    @Override
    public Map<Integer, List<DataFormatFromPredictDTO>> predictRequestStatusWithMonth(int month)
    {
        List<CountStatusRequestStatDTO> data = statGetCountRequestStatusRepoInterface.getCountRequestStatusMonth(month);
        Map<Integer, List<CountStatusRequestStatDTO>> groupingData = grouping(data);
        Map<Integer, List<DataFormatFromPredictDTO>> predictedData = new HashMap<>();

        groupingData.forEach((statusCode, statusData) -> {
            List<DataFormatForPredictDTO> convertedData = convertDataToPredict.convertData(
                    statusData,
                    CountStatusRequestStatDTO::getDate,
                    CountStatusRequestStatDTO::getCount,
                    CountStatusRequestStatDTO::getPredict
            );

            if(!predictMethodInterface.modelExists("predictRequestStatusWithMonth"))
            {
                predictMethodInterface.train(
                        "predictRequestStatusWithMonth",
                        convertedData,
                        SeasonPeriodForPredict.getSeasonPeriod(AggregationType.MONTHLY),
                        "add"
                );
            }

            double error = checkAccuracy.checkAccuracy(convertedData, TypePredictAccuracy.RMSE);

            if (error > 0.15)
            {
                optimizer.optimize(
                        convertedData,
                        SeasonPeriodForPredict.getSeasonPeriod(AggregationType.MONTHLY),
                        "add",
                        AggregationType.MONTHLY,
                        TypePredictAccuracy.RMSE
                );

                predictMethodInterface.train(
                        "predictRequestStatusWithMonth",
                        convertedData,
                        SeasonPeriodForPredict.getSeasonPeriod(AggregationType.MONTHLY),
                        "add"
                );
            }

            predictedData.put(
                    statusCode,
                    predictMethodInterface.predict(
                            "predictRequestStatusWithMonth",
                            convertedData,
                            PredictReriodForPredict.getPredictReriod(AggregationType.MONTHLY),
                            AggregationType.MONTHLY
                    )
            );
        });

        return predictedData;
    }
}
