package ru.pstu.lamsv2.subMethods.methodsForEnums;

import ru.pstu.lamsv2.enums.AggregationType;

/**
    Класс для получения сезонности метода Хольта-Уинтерса в зависимости от выбранной агрегации.
        1. Для выборки с агрегацией по часам - сезонность 168 - ровно 7 дней
        2. Для выборки с агрегацией по дням - сезонность 7 - ровно неделя
        3. Для выборки с агрегацией по месяцам - сезонность 12 - ровно год
*/

//Получение сезонности
public class SeasonPeriodForPredict
{
    public static int getSeasonPeriod(AggregationType aggregationType)
    {
        int length = 0;
        switch (aggregationType)
        {
            case HOURLY ->
                    length = 168;

            case DAILY ->
                    length = 7;

            case MONTHLY ->
                    length = 12;
        }
        return length;
    }
}
