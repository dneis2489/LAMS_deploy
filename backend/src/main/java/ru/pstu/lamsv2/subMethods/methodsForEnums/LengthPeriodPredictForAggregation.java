package ru.pstu.lamsv2.subMethods.methodsForEnums;

import ru.pstu.lamsv2.enums.AggregationType;

/**
    Класс для получения значения количества данных получаемых из БД для прогноза.
        1. Для выборки с агрегацией по часам - сезонность 168 - ровно 7 дней
        2. Для выборки с агрегацией по дням - сезонность 7 - ровно неделя
        3. Для выборки с агрегацией по месяцам - сезонность 12 - ровно год
*/

public class LengthPeriodPredictForAggregation
{
    public static int getLength(AggregationType aggregationType)
    {
        int length = 0;
        switch (aggregationType)
        {
            case HOURLY ->
                    length = 350;

            case DAILY ->
                    length = 20;

            case MONTHLY ->
                    length = 30;
        }
        return length;
    }
}
