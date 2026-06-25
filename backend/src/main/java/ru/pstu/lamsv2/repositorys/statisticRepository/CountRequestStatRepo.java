package ru.pstu.lamsv2.repositorys.statisticRepository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.CountRequestForMethodsStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.CountRequestStatDTO;
import ru.pstu.lamsv2.interfaces.statisticIntefaces.StatGetCountRequestRepoInterface;

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
    Репозиторий реализующий методы получения данных для статистики по количеству запросов. Содержит методы:
        1. Получение статистики по количеству запросов по методам микросвервисов с агрегацией по часам/дням/месяцам
        2. Получение общего количества запросов к системе с агрегацией по часам/дням/месяцам
*/

@Repository
public class CountRequestStatRepo implements StatGetCountRequestRepoInterface
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CountRequestStatRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //Получение количества запоросов по методам микросервисов с агрегацией по часам
    @Override
    public List<CountRequestForMethodsStatDTO> getCountRequestForMethodsWithHour(int hour)
    {
        LocalDateTime sinceDateTime = LocalDateTime.now().minusHours(hour);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDateTime", sinceDateTime);

        String sql = """
                    SELECT
                        crsh.stat_hour,
                    	ms.microservice_name,
                    	a.action_rus,
                        crsh.request_count,
                        crsh.predict
                    FROM public.count_request_stats_hour crsh
                    LEFT JOIN public.microservices ms ON crsh.microservice_id = ms.id
                    LEFT JOIN public.action_methods a ON crsh.action_method_id = a.id
                    WHERE crsh.stat_hour >= :sinceDateTime
                    ORDER BY stat_hour;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new CountRequestForMethodsStatDTO(
                        rs.getTimestamp("stat_hour").toLocalDateTime(),
                        rs.getString("microservice_name"),
                        rs.getString("action_rus"),
                        getNullableLong(rs, "request_count"),
                        rs.getDouble("predict")
                ));
    }

    //Получение количества запоросов по методам микросервисов с агрегацией по дням
    @Override
    public List<CountRequestForMethodsStatDTO> getCountRequestForMethodsWithDay(int days)
    {
        LocalDate sinceDate = LocalDate.now().minusDays(days);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);

        String sql = """
                    SELECT
                        crsd.stat_day,
                    	ms.microservice_name,
                    	a.action_rus,
                        crsd.request_count,
                        crsd.predict
                    FROM public.count_request_stats_day crsd
                    LEFT JOIN public.microservices ms ON crsd.microservice_id = ms.id
                    LEFT JOIN public.action_methods a ON crsd.action_method_id = a.id
                    WHERE crsd.stat_day >= :sinceDate
                    ORDER BY stat_day;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new CountRequestForMethodsStatDTO(
                        rs.getTimestamp("stat_day").toLocalDateTime(),
                        rs.getString("microservice_name"),
                        rs.getString("action_rus"),
                        getNullableLong(rs, "request_count"),
                        rs.getDouble("predict")
                ));
    }

    //Получение количества запоросов по методам микросервисов с агрегацией по месяцам
    @Override
    public List<CountRequestForMethodsStatDTO> getCountRequestForMethodsWithMonth(int month)
    {
        LocalDateTime sinceDate = LocalDateTime.now().minusMonths(month);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);

        String sql = """
                    SELECT
                        crsm.stat_month,
                    	ms.microservice_name,
                    	a.action_rus,
                        SUM(crsm.request_count) AS total_requests,
                        crsm.predict
                    FROM public.count_request_stats_month crsm
                    LEFT JOIN public.microservices ms ON crsm.microservice_id = ms.id
                    LEFT JOIN public.action_methods a ON crsm.action_method_id = a.id
                    WHERE crsm.stat_month >= :sinceDate
                    GROUP BY crsm.stat_month, ms.microservice_name, a.action_rus, crsm.predict
                    ORDER BY stat_month;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new CountRequestForMethodsStatDTO(
                        rs.getTimestamp("stat_month").toLocalDateTime(),
                        rs.getString("microservice_name"),
                        rs.getString("action_rus"),
                        getNullableLong(rs, "total_requests"),
                        rs.getDouble("predict")
                ));
    }

    //Получение общего количества запоросов с агрегацией по часам
    @Override
    public List<CountRequestStatDTO> getCountRequestWithHour(int hour)
    {
        LocalDateTime sinceDateTime = LocalDateTime.now().minusHours(hour);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDateTime", sinceDateTime);

        String sql = """
                    SELECT
                        stat_hour,
                        request_count,
                        predict,
                        anomaly
                    FROM public.total_count_request_stats_hour
                    WHERE stat_hour >= :sinceDateTime
                    ORDER BY stat_hour;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new CountRequestStatDTO(
                        rs.getTimestamp("stat_hour").toLocalDateTime(),
                        getNullableLong(rs, "request_count"),
                        rs.getDouble("predict"),
                        rs.getBoolean("anomaly")
                ));
    }

    //Получение общего количества запоросов с агрегацией по дням
    @Override
    public List<CountRequestStatDTO> getCountRequestWithDay(int days)
    {
        LocalDate sinceDate = LocalDate.now().minusDays(days);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);

        String sql = """
                    SELECT
                        stat_day,
                        request_count,
                        predict,
                        anomaly
                    FROM public.total_count_request_stats_day
                    WHERE stat_day >= :sinceDate
                    ORDER BY stat_day;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new CountRequestStatDTO(
                        rs.getTimestamp("stat_day").toLocalDateTime(),
                        getNullableLong(rs, "request_count"),
                        rs.getDouble("predict"),
                        rs.getBoolean("anomaly")
                ));
    }

    //Получение общего количества запоросов с агрегацией по месяцам
    @Override
    public List<CountRequestStatDTO> getCountRequestWithMonth(int month)
    {
        LocalDateTime sinceDate = LocalDateTime.now().minusMonths(month);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);

        String sql = """
                    SELECT
                        stat_month,
                        request_count,
                        predict,
                        anomaly
                    FROM public.total_count_request_stats_month
                    WHERE stat_month >= :sinceDate
                    ORDER BY stat_month;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new CountRequestStatDTO(
                        rs.getTimestamp("stat_month").toLocalDateTime(),
                        getNullableLong(rs, "request_count"),
                        rs.getDouble("predict"),
                        rs.getBoolean("anomaly")
                ));
    }

    @Override
    public void updateTotalCountRequestHourAnomalies(List<LocalDateTime> checkedDates, List<LocalDateTime> anomalyDates)
    {
        updateAnomalyFlags(
                "public.total_count_request_stats_hour",
                "stat_hour",
                checkedDates,
                anomalyDates
        );
    }

    @Override
    public void updateTotalCountRequestDayAnomalies(List<LocalDateTime> checkedDates, List<LocalDateTime> anomalyDates)
    {
        updateAnomalyFlags(
                "public.total_count_request_stats_day",
                "stat_day::timestamp",
                checkedDates,
                anomalyDates
        );
    }

    @Override
    public void updateTotalCountRequestMonthAnomalies(List<LocalDateTime> checkedDates, List<LocalDateTime> anomalyDates)
    {
        updateAnomalyFlags(
                "public.total_count_request_stats_month",
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
    private static Long getNullableLong(ResultSet rs, String columnName) throws SQLException
    {
        return rs.getObject(columnName, Long.class);
    }
}
