package ru.pstu.lamsv2.subMethods.methodsForEnums;

import ru.pstu.lamsv2.enums.AggregationType;

/**
    Класс для получения диапазона значений для прогнозируемых значений в зависимости от выбранной агрегации.
        1. Для выборки с агрегацией по часам - прогнозирует данные на 12 часов вперед
        2. Для выборки с агрегацией по дням - прогнозирует данные на 7 дней вперед
        3. Для выборки с агрегацией по месяцам - прогнозирует данные на 6 месяцев вперед
*/
public class PredictReriodForPredict
{
    public static int getPredictReriod(AggregationType aggregationType)
    {
        int length = 0;
        switch (aggregationType)
        {
            case HOURLY ->
                    length = 12;

            case DAILY ->
                    length = 7;

            case MONTHLY ->
                    length = 6;
        }
        return length;
    }
}
