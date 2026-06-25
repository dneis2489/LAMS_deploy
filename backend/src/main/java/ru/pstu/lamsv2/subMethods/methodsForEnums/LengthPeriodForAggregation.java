package ru.pstu.lamsv2.subMethods.methodsForEnums;

import ru.pstu.lamsv2.enums.AggregationType;

/**
    Класс для получения диапазона значений для статистики и аномальных значений в зависимости от выбранной агрегации.
        1. Для выборки с агрегацией по часам - выходят данные за последние 48 часов
        2. Для выборки с агрегацией по дням - выходят данные за последние 14 часов
        3. Для выборки с агрегацией по месяцам - выходят данные за последние 24 часов
*/
//Получение количества данных (дата ; значение) в зависимости от типа агрегации
public class LengthPeriodForAggregation
{
    public static int getLength(AggregationType aggregationType)
    {
        int length = 0;
        switch (aggregationType)
        {
            case HOURLY ->
                    length = 48;

            case DAILY ->
                    length = 14;

            case MONTHLY ->
                    length = 24;
        }
        return length;
    }
}
