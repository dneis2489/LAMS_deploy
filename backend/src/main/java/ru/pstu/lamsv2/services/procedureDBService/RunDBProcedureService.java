package ru.pstu.lamsv2.services.procedureDBService;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.interfaces.runProcedureInDB.RunProcedureRepoInterface;
import ru.pstu.lamsv2.interfaces.runProcedureInDB.RunProcedureServiceInterface;

import java.sql.SQLException;
import java.time.OffsetDateTime;

/**
 Сервис запуска процедур на заполнение таблиц БД статистическими данными. Использует процедуры описанные в БД
*/

@Service
public class RunDBProcedureService implements RunProcedureServiceInterface
{
    final private RunProcedureRepoInterface runProcedureRepo;

    public RunDBProcedureService(RunProcedureRepoInterface runProcedureRepo)
    {
        this.runProcedureRepo = runProcedureRepo;
    }

    @Override
    public void runFullPipeline(OffsetDateTime targetHour, int monthsAhead) throws SQLException
    {
        runProcedureRepo.runFullPipeline(targetHour, monthsAhead);
    }
}
