package ru.pstu.lamsv2.interfaces.findAnomalyValueInterface.methodInterface;

import java.util.List;
import java.util.function.ToDoubleFunction;

/**
    Интерфейс для метода определения интервала нормальных значений
*/

public interface FindNormalRangeForAnomalyInterface<T>
{
    List<Double> getNormalValueRange(List<T> data, ToDoubleFunction<T> valueExtractor);
}
