package ru.pstu.lamsv2.interfaces.runProcedureInDB;

import java.sql.SQLException;
import java.time.OffsetDateTime;

/**
    Интерфейс для описания методов репозитория для запуска процедур на заполнение таблиц БД статистическими данными.
    Использует процедуры описанные в БД. Реализует один метод запуска всех процедур
*/

public interface RunProcedureRepoInterface
{
    void runFullPipeline(OffsetDateTime targetHour, int monthsAhead) throws SQLException;
}
