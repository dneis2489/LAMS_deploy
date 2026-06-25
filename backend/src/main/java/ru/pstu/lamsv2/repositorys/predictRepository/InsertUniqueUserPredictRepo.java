package ru.pstu.lamsv2.repositorys.predictRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.UniqueUserMethodForecastDTO;
import ru.pstu.lamsv2.interfaces.predictInterface.repoInterface.PredictUniqueUserRepoInterface;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
    Репозиторий реализующий методы обновления прогноза по количеству уникальных пользователей системы. Включает в себя методы:
        1. Обновление таблицы данных с градацией по часам
        2. Обновление таблицы данных с градацией по дням
        3. Обновление таблицы данных с градацией по месяцам
*/

@Repository
public class InsertUniqueUserPredictRepo implements PredictUniqueUserRepoInterface
{
    private final JdbcTemplate jdbcTemplate;
    public InsertUniqueUserPredictRepo(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Обновить прогноз по количеству уникальных пользователей с градацией по часам
    @Override
    public void updateUniqueUserWithHourInDB(List<DataFormatFromPredictDTO> forecastList)
    {
        if (forecastList == null || forecastList.isEmpty())
        {
            return;
        }

        String sql = """
            INSERT INTO public.unique_users_stats_hour
                (
                    stat_hour,
                    unique_users_count,
                    unique_users,
                    predict
                )
                VALUES (?, 0, ARRAY[]::text[], ?)
            
                ON CONFLICT(stat_hour)
                DO UPDATE SET
                    predict = EXCLUDED.predict
        """;

        jdbcTemplate.batchUpdate(
                sql,
                forecastList,
                forecastList.size(),
                (ps, dto) -> {

                    ps.setTimestamp(
                            1,
                            Timestamp.valueOf(dto.getDate())
                    );

                    ps.setLong(
                            2,
                            toStoredPrediction(dto.getData())
                    );
                }
        );
    }

    @Override
    public void updateUniqueUserForMethodsWithHourInDB(List<UniqueUserMethodForecastDTO> forecastList)
    {
        String sql = """
            INSERT INTO public.unique_users_stats_method_hour
                (
                    stat_hour,
                    microservice_id,
                    action_method_id,
                    unique_users_count,
                    unique_users,
                    predict
                )
                VALUES (?, ?, ?, 0, ARRAY[]::text[], ?)

                ON CONFLICT(stat_hour, microservice_id, action_method_id)
                DO UPDATE SET
                    predict = EXCLUDED.predict
        """;

        updateUniqueUserForMethodsInDB(sql, forecastList, true);
    }

    //Обновить прогноз по количеству уникальных пользователей с градацией по дням
    @Override
    public void updateUniqueUserWithDayInDB(List<DataFormatFromPredictDTO> forecastList)
    {
        if (forecastList == null || forecastList.isEmpty())
        {
            return;
        }

        String sql = """
            INSERT INTO public.unique_users_stats_day
                (
                    stat_day,
                    unique_users_count,
                    unique_users,
                    predict
                )
                VALUES (?, 0, ARRAY[]::text[], ?)
            
                ON CONFLICT(stat_day)
                DO UPDATE SET
                    predict = EXCLUDED.predict
        """;

        jdbcTemplate.batchUpdate(
                sql,
                forecastList,
                forecastList.size(),
                (ps, dto) -> {

                    ps.setDate(
                            1,
                            Date.valueOf(dto.getDate().toLocalDate())
                    );

                    ps.setLong(
                            2,
                            toStoredPrediction(dto.getData())
                    );
                }
        );
    }

    @Override
    public void updateUniqueUserForMethodsWithDayInDB(List<UniqueUserMethodForecastDTO> forecastList)
    {
        String sql = """
            INSERT INTO public.unique_users_stats_method_day
                (
                    stat_day,
                    microservice_id,
                    action_method_id,
                    unique_users_count,
                    unique_users,
                    predict
                )
                VALUES (?, ?, ?, 0, ARRAY[]::text[], ?)

                ON CONFLICT(stat_day, microservice_id, action_method_id)
                DO UPDATE SET
                    predict = EXCLUDED.predict
        """;

        updateUniqueUserForMethodsInDB(sql, forecastList, false);
    }

    //Обновить прогноз по количеству уникальных пользователей с градацией по месяцам
    @Override
    public void updateUniqueUserWithMonthInDB(List<DataFormatFromPredictDTO> forecastList)
    {
        if (forecastList == null || forecastList.isEmpty())
        {
            return;
        }

        String sql = """
            INSERT INTO public.unique_users_stats_month
                (
                    stat_month,
                    unique_users_count,
                    unique_users,
                    predict
                )
                VALUES (?, 0, ARRAY[]::text[], ?)
            
                ON CONFLICT(stat_month)
                DO UPDATE SET
                    predict = EXCLUDED.predict
        """;

        jdbcTemplate.batchUpdate(
                sql,
                forecastList,
                forecastList.size(),
                (ps, dto) -> {

                    ps.setDate(
                            1,
                            Date.valueOf(dto.getDate().toLocalDate())
                    );

                    ps.setLong(
                            2,
                            toStoredPrediction(dto.getData())
                    );
                }
        );
    }

    @Override
    public void updateUniqueUserForMethodsWithMonthInDB(List<UniqueUserMethodForecastDTO> forecastList)
    {
        String sql = """
            INSERT INTO public.unique_users_stats_method_month
                (
                    stat_month,
                    microservice_id,
                    action_method_id,
                    unique_users_count,
                    unique_users,
                    predict
                )
                VALUES (?, ?, ?, 0, ARRAY[]::text[], ?)

                ON CONFLICT(stat_month, microservice_id, action_method_id)
                DO UPDATE SET
                    predict = EXCLUDED.predict
        """;

        updateUniqueUserForMethodsInDB(sql, forecastList, false);
    }

    private void updateUniqueUserForMethodsInDB(
            String sql,
            List<UniqueUserMethodForecastDTO> forecastList,
            boolean useTimestamp
    )
    {
        List<MethodForecastRow> rows = flattenMethodForecastRows(forecastList);

        if (rows.isEmpty())
        {
            return;
        }

        jdbcTemplate.batchUpdate(
                sql,
                rows,
                rows.size(),
                (ps, row) -> {
                    setForecastDate(ps, 1, row.forecast(), useTimestamp);
                    ps.setLong(2, row.microserviceId());
                    ps.setLong(3, row.actionMethodId());
                    ps.setLong(4, toStoredPrediction(row.forecast().getData()));
                }
        );
    }

    private List<MethodForecastRow> flattenMethodForecastRows(List<UniqueUserMethodForecastDTO> forecastList)
    {
        if (forecastList == null || forecastList.isEmpty())
        {
            return List.of();
        }

        return forecastList.stream()
                .filter(group -> group.getMicroserviceId() != null && group.getActionMethodId() != null)
                .filter(group -> group.getForecastList() != null && !group.getForecastList().isEmpty())
                .flatMap(group -> group.getForecastList().stream()
                        .filter(forecast -> forecast != null && forecast.getDate() != null)
                        .map(forecast -> new MethodForecastRow(
                                group.getMicroserviceId(),
                                group.getActionMethodId(),
                                forecast
                        )))
                .toList();
    }

    private static void setForecastDate(
            PreparedStatement ps,
            int parameterIndex,
            DataFormatFromPredictDTO forecast,
            boolean useTimestamp
    ) throws SQLException
    {
        if (useTimestamp)
        {
            ps.setTimestamp(parameterIndex, Timestamp.valueOf(forecast.getDate()));
            return;
        }

        ps.setDate(parameterIndex, Date.valueOf(forecast.getDate().toLocalDate()));
    }

    private static long toStoredPrediction(double value)
    {
        if (!Double.isFinite(value))
        {
            return 0;
        }

        return Math.max(0L, Math.round(value));
    }

    private record MethodForecastRow(
            Long microserviceId,
            Long actionMethodId,
            DataFormatFromPredictDTO forecast
    )
    {
    }
}
