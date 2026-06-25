package ru.pstu.lamsv2.repositorys.procedureDBRepository;

import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.interfaces.runProcedureInDB.RunProcedureRepoInterface;

import java.sql.*;
import java.time.OffsetDateTime;
import javax.sql.DataSource;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
    Репозиторий запуска процедур на заполнение таблиц БД статистическими данными. Использует процедуры описанные в БД
*/

@Repository
public class RunDBProcedureRepo implements RunProcedureRepoInterface
{
    private static final Logger LOG = Logger.getLogger(RunDBProcedureRepo.class.getName());
    private final DataSource dataSource;
    private final int queryTimeoutSeconds = 180; // 3 минуты на одну процедуру

    public RunDBProcedureRepo(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    // 🔹 1. Создание партиций (единственная процедура с INT параметром)
    public void ensureLogsPartitions(int monthsAhead) throws SQLException
    {
        String sql = "CALL public.ensure_logs_partitions(?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, monthsAhead);
            stmt.setQueryTimeout(queryTimeoutSeconds);
            stmt.execute();
        }
    }

    // 🔹 2-9. Все процедуры с параметром p_run_time timestamptz
    // Вынесено в общий метод для избежания дублирования
    private void callTimeBasedProcedure(String procedureName, OffsetDateTime runTime) throws SQLException
    {
        String sql = "CALL public." + procedureName + "(?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            // PostgreSQL JDBC 4.2+ корректно маппит OffsetDateTime на TIMESTAMPTZ
            stmt.setObject(1, runTime);
            stmt.setQueryTimeout(queryTimeoutSeconds);
            stmt.execute();
        }
    }

    public void fillCountStats(OffsetDateTime runTime) throws SQLException
    {
        callTimeBasedProcedure("fill_count_request_stats_incremental", runTime);
    }

    public void fillMethodLogTypeStats(OffsetDateTime runTime) throws SQLException
    {
        callTimeBasedProcedure("fill_method_log_type_stats_incremental", runTime);
    }

    public void fillMethodDurationStats(OffsetDateTime runTime) throws SQLException
    {
        callTimeBasedProcedure("fill_method_duration_stats_incremental", runTime);
    }

    public void fillMethodRequestStatusStats(OffsetDateTime runTime) throws SQLException
    {
        callTimeBasedProcedure("fill_method_request_status_stats_incremental", runTime);
    }

    public void fillTotalCountStats(OffsetDateTime runTime) throws SQLException
    {
        callTimeBasedProcedure("fill_total_count_request_stats_incremental", runTime);
    }

    public void fillUniqueUsersStats(OffsetDateTime runTime) throws SQLException
    {
        callTimeBasedProcedure("fill_unique_users_stats_incremental", runTime);
    }

    public void fillTotalRequestStatusStats(OffsetDateTime runTime) throws SQLException
    {
        callTimeBasedProcedure("fill_total_request_status_stats_incremental", runTime);
    }

    public void fillTotalDurationStats(OffsetDateTime runTime) throws SQLException
    {
        callTimeBasedProcedure("fill_total_duration_stats_incremental", runTime);
    }

    // 🌟 АТОМАРНЫЙ ЗАПУСК ВСЕХ ПРОЦЕДУР В ОДНОЙ ТРАНЗАКЦИИ
    @Override
    public void runFullPipeline(OffsetDateTime targetHour, int monthsAhead) throws SQLException
    {
        try (Connection conn = dataSource.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                LOG.info("Начало агрегации статистики за период до: " + targetHour);

                // 1. Гарантируем наличие партиций
                callProcedure(conn, "CALL public.ensure_logs_partitions(?)", monthsAhead);

                // 2. Все агрегации (порядок не критичен, т.к. они независимы)
                String[] statsProcedures =
                        {
                        "fill_count_request_stats_incremental",
                        "fill_method_log_type_stats_incremental",
                        "fill_method_duration_stats_incremental",
                        "fill_method_request_status_stats_incremental",
                        "fill_total_count_request_stats_incremental",
                        "fill_unique_users_stats_incremental",
                        "fill_total_request_status_stats_incremental",
                        "fill_total_duration_stats_incremental"
                };

                for (String proc : statsProcedures)
                {
                    callProcedure(conn, "CALL public." + proc + "(?)", targetHour);
                }

                conn.commit();
                LOG.info("Агрегация успешно завершена.");
            }
            catch (SQLException e)
            {
                conn.rollback();
                LOG.log(Level.SEVERE, "Ошибка агрегации, транзакция откачена", e);
                throw e;
            }
        }
    }

    // Вспомогательный метод для вызова внутри одной транзакции
    private void callProcedure(Connection conn, String sql, Object... params) throws SQLException
    {
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            for (int i = 0; i < params.length; i++)
            {
                Object p = params[i];
                if (p instanceof Integer) stmt.setInt(i + 1, (Integer) p);
                else stmt.setObject(i + 1, p);
            }
            stmt.setQueryTimeout(queryTimeoutSeconds);
            stmt.execute();
        }
    }
}
