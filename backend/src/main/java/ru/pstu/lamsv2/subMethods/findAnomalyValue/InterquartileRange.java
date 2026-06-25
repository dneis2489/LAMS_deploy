package ru.pstu.lamsv2.subMethods.findAnomalyValue;

import org.springframework.stereotype.Component;
import ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.methodInterface.FindNormalRangeForAnomalyInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

/**
    Компонент реализующий метод межквартильного размаха для определения интервала нормальных значений.
        Все значения выходящие за найденный интервал - аномальные
*/

@Component
public class InterquartileRange<T> implements FindNormalRangeForAnomalyInterface<T>
{
    //Получить интервал нормальных значений
    @Override
    public List<Double> getNormalValueRange(
            List<T> data,
            ToDoubleFunction<T> valueExtractor
    )
    {
        if (data == null || data.isEmpty())
        {
            return List.of(0.0, 0.0);
        }

        List<Double> values = new ArrayList<>();

        for (T item : data)
        {
            values.add(valueExtractor.applyAsDouble(item));
        }

        Collections.sort(values);

        double q1 = getPercentile(values, 25);
        double q3 = getPercentile(values, 75);

        double iqr = q3 - q1;

        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;

        return List.of(lowerBound, upperBound);
    }

    //Получить процентиль из данных для реализации метода мехквартильного размаха
    private double getPercentile(List<Double> sortedValues, double percentile)
    {
        if (sortedValues.size() == 1)
        {
            return sortedValues.get(0);
        }

        double index = percentile / 100.0 * (sortedValues.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);

        if (lowerIndex == upperIndex)
        {
            return sortedValues.get(lowerIndex);
        }

        double weight = index - lowerIndex;

        return sortedValues.get(lowerIndex) * (1 - weight)
                + sortedValues.get(upperIndex) * weight;
    }
}
