package ru.pstu.lamsv2.enums;

public enum NotificationCategory
{
    ANOMALY_DETECTED("Зафиксировано аномальное значение"),
    ERROR_FORECAST("Спрогнозировано большое количество ошибок в следующем часу"),
    ERROR_LOG_RECEIVED("Пришел лог с ошибкой");

    private final String title;

    NotificationCategory(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }
}
