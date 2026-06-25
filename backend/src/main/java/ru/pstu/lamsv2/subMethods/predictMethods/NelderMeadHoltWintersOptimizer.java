package ru.pstu.lamsv2.subMethods.predictMethods;

import org.springframework.stereotype.Component;
import ru.pstu.lamsv2.config.prediction.HoltWintersConfig;
import ru.pstu.lamsv2.dto.application.predictDTO.DataFormatForPredictDTO;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.enums.AggregationType;
import ru.pstu.lamsv2.enums.TypePredictAccuracy;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.CheckPredictAccuracyInterface;
import ru.pstu.lamsv2.interfaces.predictInterface.methodInterface.HoltWintersOptimizationInterface;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 Компонент реализующий методы поиска оптимальных коэффициентов. Используется для поиска коэффициентов для модели Хольта-Уинтерса.
    Поиск происходит через метод Нелдера-Мида
*/

@Component
public class NelderMeadHoltWintersOptimizer implements HoltWintersOptimizationInterface
{
    private final HoltWintersConfig config;
    private final HoltWintersPredict holtWintersPredict;
    private final CheckPredictAccuracyInterface checkAccuracy;

    public NelderMeadHoltWintersOptimizer(
            HoltWintersConfig config,
            HoltWintersPredict holtWintersPredict,
            CheckPredictAccuracyInterface checkAccuracy
    )
    {
        this.config = config;
        this.holtWintersPredict = holtWintersPredict;
        this.checkAccuracy = checkAccuracy;
    }


    @Override
    public HoltWintersConfig optimize(
            List<DataFormatForPredictDTO> data,
            int seasonPeriod,
            String typeModel,
            AggregationType aggregationType,
            TypePredictAccuracy accuracyType
    )
    {
        if (data == null || seasonPeriod <= 0 || data.size() < seasonPeriod * 3)
        {
            return config;
        }

        int validationSize = seasonPeriod;

        if (data.size() <= seasonPeriod * 2)
        {
            throw new IllegalArgumentException("Недостаточно данных для оптимизации коэффициентов");
        }

        List<DataFormatForPredictDTO> trainData =
                data.subList(0, data.size() - validationSize);

        List<DataFormatForPredictDTO> validationData =
                data.subList(data.size() - validationSize, data.size());

        double originalAlpha = config.getAlpha();
        double originalBeta = config.getBeta();
        double originalGamma = config.getGamma();

        try
        {
            List<Point> simplex = new ArrayList<>();

            simplex.add(new Point(originalAlpha, originalBeta, originalGamma));
            simplex.add(new Point(clamp(originalAlpha + 0.1), originalBeta, originalGamma));
            simplex.add(new Point(originalAlpha, clamp(originalBeta + 0.1), originalGamma));
            simplex.add(new Point(originalAlpha, originalBeta, clamp(originalGamma + 0.1)));

            int maxIterations = 100;
            double tolerance = 1e-6;

            double reflection = 1.0;
            double expansion = 2.0;
            double contraction = 0.5;
            double shrink = 0.5;

            for (int iteration = 0; iteration < maxIterations; iteration++)
            {
                for (Point point : simplex)
                {
                    point.error = calculateError(
                            point,
                            trainData,
                            validationData,
                            seasonPeriod,
                            typeModel,
                            aggregationType,
                            accuracyType
                    );
                }

                simplex.sort(Comparator.comparingDouble(p -> p.error));

                Point best = simplex.get(0);
                Point secondWorst = simplex.get(2);
                Point worst = simplex.get(3);

                if (Math.abs(worst.error - best.error) < tolerance)
                {
                    break;
                }

                Point centroid = centroid(simplex);

                Point reflected = centroid.moveFrom(worst, reflection);
                reflected.error = calculateError(
                        reflected,
                        trainData,
                        validationData,
                        seasonPeriod,
                        typeModel,
                        aggregationType,
                        accuracyType
                );

                if (reflected.error < best.error)
                {
                    Point expanded = centroid.moveFrom(worst, expansion);
                    expanded.error = calculateError(
                            expanded,
                            trainData,
                            validationData,
                            seasonPeriod,
                            typeModel,
                            aggregationType,
                            accuracyType
                    );

                    simplex.set(3, expanded.error < reflected.error ? expanded : reflected);
                }
                else if (reflected.error < secondWorst.error)
                {
                    simplex.set(3, reflected);
                }
                else
                {
                    Point contracted = centroid.moveFrom(worst, contraction);
                    contracted.error = calculateError(
                            contracted,
                            trainData,
                            validationData,
                            seasonPeriod,
                            typeModel,
                            aggregationType,
                            accuracyType
                    );

                    if (contracted.error < worst.error)
                    {
                        simplex.set(3, contracted);
                    }
                    else
                    {
                        for (int i = 1; i < simplex.size(); i++)
                        {
                            simplex.set(i, simplex.get(i).shrinkTo(best, shrink));
                        }
                    }
                }
            }

            simplex.sort(Comparator.comparingDouble(p -> p.error));
            Point best = simplex.get(0);

            config.setAlpha(best.alpha);
            config.setBeta(best.beta);
            config.setGamma(best.gamma);

            return config;

        }
        catch (RuntimeException e)
        {
            config.setAlpha(originalAlpha);
            config.setBeta(originalBeta);
            config.setGamma(originalGamma);
            throw e;
        }
    }

    private double calculateError(
            Point point,
            List<DataFormatForPredictDTO> trainData,
            List<DataFormatForPredictDTO> validationData,
            int seasonPeriod,
            String typeModel,
            AggregationType aggregationType,
            TypePredictAccuracy accuracyType
    )
    {
        config.setAlpha(point.alpha);
        config.setBeta(point.beta);
        config.setGamma(point.gamma);

        String modelName = "nelder-mead-" + UUID.randomUUID();

        holtWintersPredict.train(
                modelName,
                trainData,
                seasonPeriod,
                typeModel
        );

        List<DataFormatFromPredictDTO> predictions = holtWintersPredict.predict(
                modelName,
                trainData,
                validationData.size(),
                aggregationType
        );

        if (predictions.size() < validationData.size())
        {
            return Double.POSITIVE_INFINITY;
        }

        List<DataFormatForPredictDTO> accuracyData = new ArrayList<>();

        for (int i = 0; i < validationData.size(); i++)
        {
            DataFormatForPredictDTO actual = validationData.get(i);
            actual.setPrediction(predictions.get(i).getData());
            accuracyData.add(actual);
        }

        return checkAccuracy.checkAccuracy(accuracyData, accuracyType);
    }

    private Point centroid(List<Point> simplex) {
        double alpha = 0;
        double beta = 0;
        double gamma = 0;

        for (int i = 0; i < simplex.size() - 1; i++)
        {
            alpha += simplex.get(i).alpha;
            beta += simplex.get(i).beta;
            gamma += simplex.get(i).gamma;
        }

        int count = simplex.size() - 1;

        return new Point(
                alpha / count,
                beta / count,
                gamma / count
        );
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private class Point
    {
        double alpha;
        double beta;
        double gamma;
        double error;

        Point(double alpha, double beta, double gamma)
        {
            this.alpha = clamp(alpha);
            this.beta = clamp(beta);
            this.gamma = clamp(gamma);
        }

        Point moveFrom(Point worst, double coefficient)
        {
            return new Point(
                    alpha + coefficient * (alpha - worst.alpha),
                    beta + coefficient * (beta - worst.beta),
                    gamma + coefficient * (gamma - worst.gamma)
            );
        }

        Point shrinkTo(Point best, double coefficient)
        {
            return new Point(
                    best.alpha + coefficient * (alpha - best.alpha),
                    best.beta + coefficient * (beta - best.beta),
                    best.gamma + coefficient * (gamma - best.gamma)
            );
        }
    }
}
