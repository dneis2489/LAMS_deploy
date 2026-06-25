package ru.pstu.lamsv2.repositorys.statisticRepository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.CountStatusRequestForMethodsStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.CountStatusRequestStatDTO;
import ru.pstu.lamsv2.interfaces.statisticIntefaces.StatGetCountRequestStatusRepoInterface;

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
    Репозиторий реализующий методы получения данных для статистики по статусам ответов. Содержит методы:
        1. Получение статистики по статусам ответов для каждого микросервиса с агрегацией по часам/дням/месяцам
        2. Получение общего количества статусов ответов к системе с агрегацией по часам/дням/месяцам
*/

@Repository
public class CountRequestStatusStatRepo implements StatGetCountRequestStatusRepoInterface
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CountRequestStatusStatRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //Получение количества типов ответа по методам микросервисов с агрегацией по часам
    @Override
    public List<CountStatusRequestForMethodsStatDTO> getCountRequestStatusForMethodsWithHour(int hour)
    {
        LocalDateTime sinceDateTime = LocalDateTime.now().minusHours(hour);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDateTime", sinceDateTime);

        String sql = """
                    SELECT
                        mrssh.stat_hour,
                    	ms.microservice_name,
                    	a.action_rus,
                    	rs.request_status_code,
                        mrssh.request_count,
                        mrssh.predict
                    FROM public.method_request_status_stats_hour mrssh
                    LEFT JOIN public.microservices ms ON mrssh.microservice_id = ms.id
                    LEFT JOIN public.action_methods a ON mrssh.action_method_id = a.id
                    LEFT JOIN public.request_status rs ON mrssh.request_status_id = rs.id
                    WHERE mrssh.stat_hour >= :sinceDateTime
                    ORDER BY mrssh.stat_hour;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new CountStatusRequestForMethodsStatDTO(
                        rs.getTimestamp("stat_hour").toLocalDateTime(),
                        rs.getString("microservice_name"),
                        rs.getString("action_rus"),
                        rs.getInt("request_status_code"),
                        getNullableLong(rs, "request_count"),
                        rs.getDouble("predict")
                ));
    }

    //Получение количества типов ответа по методам микросервисов с агрегацией по дням
    @Override
    public List<CountStatusRequestForMethodsStatDTO> getCountRequestStatusForMethodsWithDay(int days)
    {
        LocalDate sinceDate = LocalDate.now().minusDays(days);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);

        String sql = """
                    SELECT
                        mrssd.stat_day,
                    	ms.microservice_name,
                    	a.action_rus,
                    	rs.request_status_code,
                        mrssd.request_count,
                        mrssd.predict
                    FROM public.method_request_status_stats_day mrssd
                    LEFT JOIN public.microservices ms ON mrssd.microservice_id = ms.id
                    LEFT JOIN public.action_methods a ON mrssd.action_method_id = a.id
                    LEFT JOIN public.request_status rs ON mrssd.request_status_id = rs.id
                    WHERE mrssd.stat_day >= :sinceDate
                    ORDER BY mrssd.stat_day;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new CountStatusRequestForMethodsStatDTO(
                        rs.getTimestamp("stat_day").toLocalDateTime(),
                        rs.getString("microservice_name"),
                        rs.getString("action_rus"),
                        rs.getInt("request_status_code"),
                        getNullableLong(rs, "request_count"),
                        rs.getDouble("predict")
                ));
    }

    //Получение количества типов ответа по методам микросервисов с агрегацией по месяцам
    @Override
    public List<CountStatusRequestForMethodsStatDTO> getCountRequestStatusForMethodsMonth(int month)
    {
        LocalDateTime sinceDate = LocalDateTime.now().minusMonths(month);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);

        String sql = """
                    SELECT
                        mrssm.stat_month,
                    	ms.microservice_name,
                    	a.action_rus,
                    	rs.request_status_code,
                        mrssm.request_count,
                        mrssm.predict
                    FROM public.method_request_status_stats_month mrssm
                    LEFT JOIN public.microservices ms ON mrssm.microservice_id = ms.id
                    LEFT JOIN public.action_methods a ON mrssm.action_method_id = a.id
                    LEFT JOIN public.request_status rs ON mrssm.request_status_id = rs.id
                    WHERE mrssm.stat_month >= :sinceDate
                    ORDER BY mrssm.stat_month;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new CountStatusRequestForMethodsStatDTO(
                        rs.getTimestamp("stat_month").toLocalDateTime(),
                        rs.getString("microservice_name"),
                        rs.getString("action_rus"),
                        rs.getInt("request_status_code"),
                        getNullableLong(rs, "request_count"),
                        rs.getDouble("predict")
                ));
    }

    //Получение общего количества типов ответа с агрегацией по часам
    @Override
    public List<CountStatusRequestStatDTO> getCountRequestStatusWithHour(int hour)
    {
        LocalDateTime sinceDateTime = LocalDateTime.now().minusHours(hour);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDateTime", sinceDateTime);

        String sql = """
                    SELECT
                        stat_hour,
                        rs.request_status_code,
                        request_count,
                        predict,
                        anomaly
                    FROM public.total_request_status_stats_hour
                    LEFT JOIN public.request_status rs ON request_status_id = rs.id
                    WHERE stat_hour >= :sinceDateTime
                    ORDER BY stat_hour;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new CountStatusRequestStatDTO(
                        rs.getTimestamp("stat_hour").toLocalDateTime(),
                        rs.getInt("request_status_code"),
                        getNullableLong(rs, "request_count"),
                        rs.getDouble("predict"),
                        rs.getBoolean("anomaly")
                ));
    }

    //Получение общего количества типов ответа с агрегацией по дням
    @Override
    public List<CountStatusRequestStatDTO> getCountRequestStatusWithDay(int days)
    {
        LocalDate sinceDate = LocalDate.now().minusDays(days);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);

        String sql = """
                    SELECT
                        stat_day,
                        rs.request_status_code,
                        request_count,
                        predict,
                        anomaly
                    FROM public.total_request_status_stats_day
                    LEFT JOIN public.request_status rs ON request_status_id = rs.id
                    WHERE stat_day >= :sinceDate
                    ORDER BY stat_day;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new CountStatusRequestStatDTO(
                        rs.getTimestamp("stat_day").toLocalDateTime(),
                        rs.getInt("request_status_code"),
                        getNullableLong(rs, "request_count"),
                        rs.getDouble("predict"),
                        rs.getBoolean("anomaly")
                ));
    }

    //Получение общего количества типов ответа с агрегацией по месяцам
    @Override
    public List<CountStatusRequestStatDTO> getCountRequestStatusMonth(int month)
    {
        LocalDateTime sinceDate = LocalDateTime.now().minusMonths(month);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);

        String sql = """
                    SELECT
                        stat_month,
                        rs.request_status_code,
                        request_count,
                        predict,
                        anomaly
                    FROM public.total_request_status_stats_month
                    LEFT JOIN public.request_status rs ON request_status_id = rs.id
                    WHERE stat_month >= :sinceDate
                    ORDER BY stat_month;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new CountStatusRequestStatDTO(
                        rs.getTimestamp("stat_month").toLocalDateTime(),
                        rs.getInt("request_status_code"),
                        getNullableLong(rs, "request_count"),
                        rs.getDouble("predict"),
                        rs.getBoolean("anomaly")
                ));
    }

    @Override
    public void updateTotalRequestStatusHourAnomalies(List<CountStatusRequestStatDTO> checkedRows, List<CountStatusRequestStatDTO> anomalyRows)
    {
        updateAnomalyFlags(
                "public.total_request_status_stats_hour",
                "stat_hour = :date",
                checkedRows,
                anomalyRows
        );
    }

    @Override
    public void updateTotalRequestStatusDayAnomalies(List<CountStatusRequestStatDTO> checkedRows, List<CountStatusRequestStatDTO> anomalyRows)
    {
        updateAnomalyFlags(
                "public.total_request_status_stats_day",
                "stat_day = CAST(:date AS date)",
                checkedRows,
                anomalyRows
        );
    }

    @Override
    public void updateTotalRequestStatusMonthAnomalies(List<CountStatusRequestStatDTO> checkedRows, List<CountStatusRequestStatDTO> anomalyRows)
    {
        updateAnomalyFlags(
                "public.total_request_status_stats_month",
                "stat_month = CAST(:date AS date)",
                checkedRows,
                anomalyRows
        );
    }

    private void updateAnomalyFlags(String tableName, String dateCondition, List<CountStatusRequestStatDTO> checkedRows, List<CountStatusRequestStatDTO> anomalyRows)
    {
        if (checkedRows == null || checkedRows.isEmpty())
        {
            return;
        }

        batchUpdateAnomaly(tableName, dateCondition, checkedRows, false);

        if (anomalyRows == null || anomalyRows.isEmpty())
        {
            return;
        }

        batchUpdateAnomaly(tableName, dateCondition, anomalyRows, true);
    }

    private void batchUpdateAnomaly(String tableName, String dateCondition, List<CountStatusRequestStatDTO> rows, boolean anomaly)
    {
        String sql = "UPDATE " + tableName + " t "
                + "SET anomaly = :anomaly "
                + "FROM public.request_status rs "
                + "WHERE t.request_status_id = rs.id "
                + "AND rs.request_status_code = :statusCode "
                + "AND t." + dateCondition;

        MapSqlParameterSource[] params = rows.stream()
                .map(row -> new MapSqlParameterSource()
                        .addValue("anomaly", anomaly)
                        .addValue("date", row.getDate())
                        .addValue("statusCode", row.getStatusCode()))
                .toArray(MapSqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, params);
    }
    private static Long getNullableLong(ResultSet rs, String columnName) throws SQLException
    {
        return rs.getObject(columnName, Long.class);
    }
}
