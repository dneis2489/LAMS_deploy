package ru.pstu.lamsv2.cron;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pstu.lamsv2.interfaces.runProcedureInDB.RunProcedureServiceInterface;

import java.sql.SQLException;
import java.time.OffsetDateTime;


/**
    Компонент для запуска методов сервиса на заполнение таблиц БД статистическими данными при помощи процедур БД.
    Использует процедуры описанные в БД. Реализует один метод запуска всех процедур
*/

@Component
public class RunProcedureWithCron
{

    final public RunProcedureServiceInterface  runProcedureServiceInterface;

    public RunProcedureWithCron(RunProcedureServiceInterface runProcedureServiceInterface)
    {
        this.runProcedureServiceInterface = runProcedureServiceInterface;
    }

    //Общее количество запросов к системе с агрегацией по часам
    @Scheduled(
            cron = "0 0 * * * *",
            zone = "Europe/Moscow"
    )
    public void updateTotalCountRequestWithHour()
    {
        OffsetDateTime targetTime = OffsetDateTime.now().withMinute(0).withSecond(0).withNano(0);
        try
        {
            runProcedureServiceInterface.runFullPipeline(targetTime, 2); // Партиции на 2 месяца вперёд
        }
        catch (SQLException e)
        {
            // Логирование, алерты, retry-логика
            System.err.println("Failed to run stats aggregation: " + e.getMessage());
        }
    }
}
