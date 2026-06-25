package ru.pstu.lamsv2.repositorys.statisticRepository;


import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.DurationForMethodsStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.DurationStatDTO;
import ru.pstu.lamsv2.interfaces.statisticIntefaces.StatGetDurationRepoInterface;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
    Репозиторий реализующий методы получения данных для статистики по длительности выполнения запросов. Содержит методы:
        1. Получение статистики по длительности выполнения запросов для каждого микросевиса с агрегацией по часам/дням/месяцам
        2. Получение общей длительности выполнения запросов системой с агрегацией по часам/дням/месяцам
*/

@Repository
public class DurationStatRepo implements StatGetDurationRepoInterface
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DurationStatRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //Получение длительности выполнения запоросов по методам микросервисов с агрегацией по часам
    @Override
    public List<DurationForMethodsStatDTO> getDurationForMethodsWithHour(int hour)
    {
        LocalDateTime sinceDateTime = LocalDateTime.now().minusHours(hour);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDateTime", sinceDateTime);

        String sql = """
                    SELECT
                        mdsh.stat_hour,
                    	ms.microservice_name,
                    	a.action_rus,
                        mdsh.min_duration_ms,
                    	mdsh.avg_duration_ms,
                    	mdsh.max_duration_ms,
                    	mdsh.avg_predict
                    FROM public.method_duration_stats_hour mdsh
                    LEFT JOIN public.microservices ms ON mdsh.microservice_id = ms.id
                    LEFT JOIN public.action_methods a ON mdsh.action_method_id = a.id
                    WHERE mdsh.stat_hour >= :sinceDateTime
                    ORDER BY stat_hour;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new DurationForMethodsStatDTO(
                        rs.getTimestamp("stat_hour").toLocalDateTime(),
                        rs.getString("microservice_name"),
                        rs.getString("action_rus"),
                        getNullableDouble(rs, "min_duration_ms"),
                        getNullableDouble(rs, "avg_duration_ms"),
                        getNullableDouble(rs, "max_duration_ms"),
                        rs.getDouble("avg_predict")
                ));
    }

    //Получение длительности выполнения запоросов по методам микросервисов с агрегацией по дням
    @Override
    public List<DurationForMethodsStatDTO> getDurationForMethodsWithDay(int days)
    {
        LocalDate sinceDate = LocalDate.now().minusDays(days);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);

        String sql = """
                    SELECT
                        mdsd.stat_day,
                    	ms.microservice_name,
                    	a.action_rus,
                        mdsd.min_duration_ms,
                    	mdsd.avg_duration_ms,
                    	mdsd.max_duration_ms,
                    	mdsd.avg_predict
                    FROM public.method_duration_stats_day mdsd
                    LEFT JOIN public.microservices ms ON mdsd.microservice_id = ms.id
                    LEFT JOIN public.action_methods a ON mdsd.action_method_id = a.id
                    WHERE mdsd.stat_day >= :sinceDate
                    ORDER BY stat_day;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new DurationForMethodsStatDTO(
                        rs.getTimestamp("stat_day").toLocalDateTime(),
                        rs.getString("microservice_name"),
                        rs.getString("action_rus"),
                        getNullableDouble(rs, "min_duration_ms"),
                        getNullableDouble(rs, "avg_duration_ms"),
                        getNullableDouble(rs, "max_duration_ms"),
                        rs.getDouble("avg_predict")
                ));
    }

    //Получение длительности выполнения запоросов по методам микросервисов с агрегацией по месяцам
    @Override
    public List<DurationForMethodsStatDTO> getDurationForMethodsWithMonth(int month)
    {
        LocalDateTime sinceDate = LocalDateTime.now().minusMonths(month);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);

        String sql = """
                    SELECT
                        mdsm.stat_month,
                    	ms.microservice_name,
                    	a.action_rus,
                        mdsm.min_duration_ms,
                    	mdsm.avg_duration_ms,
                    	mdsm.max_duration_ms,
                    	mdsm.avg_predict
                    FROM public.method_duration_stats_month mdsm
                    LEFT JOIN public.microservices ms ON mdsm.microservice_id = ms.id
                    LEFT JOIN public.action_methods a ON mdsm.action_method_id = a.id
                    WHERE mdsm.stat_month >= :sinceDate
                    ORDER BY stat_month;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new DurationForMethodsStatDTO(
                        rs.getTimestamp("stat_month").toLocalDateTime(),
                        rs.getString("microservice_name"),
                        rs.getString("action_rus"),
                        getNullableDouble(rs, "min_duration_ms"),
                        getNullableDouble(rs, "avg_duration_ms"),
                        getNullableDouble(rs, "max_duration_ms"),
                        rs.getDouble("avg_predict")
                ));
    }

    //Получение общей длительности выполнения запоросов с агрегацией по часам
    @Override
    public List<DurationStatDTO> getDurationWithHour(int hour)
    {
        LocalDateTime sinceDateTime = LocalDateTime.now().minusHours(hour);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDateTime", sinceDateTime);

        String sql = """
                    SELECT
                        stat_hour,
                    	min_duration_ms,
                        avg_duration_ms,
                    	max_duration_ms,
                    	avg_predict,
                        anomaly
                    FROM public.total_duration_stats_hour
                    WHERE stat_hour >= :sinceDateTime
                    ORDER BY stat_hour;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new DurationStatDTO(
                        rs.getTimestamp("stat_hour").toLocalDateTime(),
                        getNullableDouble(rs, "min_duration_ms"),
                        getNullableDouble(rs, "avg_duration_ms"),
                        getNullableDouble(rs, "max_duration_ms"),
                        rs.getDouble("avg_predict"),
                        rs.getBoolean("anomaly")
                ));
    }

    //Получение общей длительности выполнения запоросов с агрегацией по дням
    @Override
    public List<DurationStatDTO> getDurationWithDay(int days)
    {
        LocalDate sinceDate = LocalDate.now().minusDays(days);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);

        String sql = """
                    SELECT
                        stat_day,
                    	min_duration_ms,
                        avg_duration_ms,
                    	max_duration_ms,
                    	avg_predict,
                        anomaly
                    FROM public.total_duration_stats_day
                    WHERE stat_day >= :sinceDate
                    ORDER BY stat_day;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new DurationStatDTO(
                        rs.getTimestamp("stat_day").toLocalDateTime(),
                        getNullableDouble(rs, "min_duration_ms"),
                        getNullableDouble(rs, "avg_duration_ms"),
                        getNullableDouble(rs, "max_duration_ms"),
                        rs.getDouble("avg_predict"),
                        rs.getBoolean("anomaly")
                ));
    }

    //Получение общей длительности выполнения запоросов с агрегацией по месяцам
    @Override
    public List<DurationStatDTO> getDurationWithMonth(int month)
    {
        LocalDateTime sinceDate = LocalDateTime.now().minusMonths(month);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);

        String sql = """
                    SELECT
                        stat_month,
                    	min_duration_ms,
                        avg_duration_ms,
                    	max_duration_ms,
                    	avg_predict,
                        anomaly
                    FROM public.total_duration_stats_month
                    WHERE stat_month >= :sinceDate
                    ORDER BY stat_month;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new DurationStatDTO(
                        rs.getTimestamp("stat_month").toLocalDateTime(),
                        getNullableDouble(rs, "min_duration_ms"),
                        getNullableDouble(rs, "avg_duration_ms"),
                        getNullableDouble(rs, "max_duration_ms"),
                        rs.getDouble("avg_predict"),
                        rs.getBoolean("anomaly")
                ));
    }

    @Override
    public void updateTotalDurationHourAnomalies(List<LocalDateTime> checkedDates, List<LocalDateTime> anomalyDates)
    {
        updateAnomalyFlags(
                "public.total_duration_stats_hour",
                "stat_hour",
                checkedDates,
                anomalyDates
        );
    }

    @Override
    public void updateTotalDurationDayAnomalies(List<LocalDateTime> checkedDates, List<LocalDateTime> anomalyDates)
    {
        updateAnomalyFlags(
                "public.total_duration_stats_day",
                "stat_day::timestamp",
                checkedDates,
                anomalyDates
        );
    }

    @Override
    public void updateTotalDurationMonthAnomalies(List<LocalDateTime> checkedDates, List<LocalDateTime> anomalyDates)
    {
        updateAnomalyFlags(
                "public.total_duration_stats_month",
                "stat_month::timestamp",
                checkedDates,
                anomalyDates
        );
    }

    private void updateAnomalyFlags(String tableName, String dateColumn, List<LocalDateTime> checkedDates, List<LocalDateTime> anomalyDates)
    {
        if (checkedDates == null || checkedDates.isEmpty())
        {
            return;
        }

        Map<String, Object> resetParams = new HashMap<>();
        resetParams.put("checkedDates", checkedDates);
        namedParameterJdbcTemplate.update(
                "UPDATE " + tableName + " SET anomaly = false WHERE " + dateColumn + " IN (:checkedDates)",
                resetParams
        );

        if (anomalyDates == null || anomalyDates.isEmpty())
        {
            return;
        }

        Map<String, Object> anomalyParams = new HashMap<>();
        anomalyParams.put("anomalyDates", anomalyDates);
        namedParameterJdbcTemplate.update(
                "UPDATE " + tableName + " SET anomaly = true WHERE " + dateColumn + " IN (:anomalyDates)",
                anomalyParams
        );
    }
    private static Double getNullableDouble(ResultSet rs, String columnName) throws SQLException
    {
        Number value = (Number) rs.getObject(columnName);
        return value == null ? null : value.doubleValue();
    }
}
