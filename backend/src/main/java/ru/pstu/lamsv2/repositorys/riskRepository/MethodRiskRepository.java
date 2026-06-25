package ru.pstu.lamsv2.repositorys.riskRepository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.application.riskDTO.MethodWindowMetricsDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MethodRiskRepository
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public MethodRiskRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<MethodWindowMetricsDTO> getMethodMetrics(LocalDateTime startDate, LocalDateTime endDate)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        String sql = """
                WITH request_last_log AS (
                    SELECT DISTINCT ON (l.correlation_id)
                        l.correlation_id,
                        ms.microservice_name,
                        a.action_rus,
                        rs.request_status_code,
                        l.duration
                    FROM public.logs l
                    LEFT JOIN public.microservices ms ON l.microservice_id = ms.id
                    LEFT JOIN public.action_methods a ON l.action_method_id = a.id
                    LEFT JOIN public.request_status rs ON l.request_status_id = rs.id
                    WHERE l.log_date >= :startDate
                      AND l.log_date < :endDate
                    ORDER BY
                        l.correlation_id,
                        CASE
                            WHEN l.duration IS NOT NULL OR l.request_status_id IS NOT NULL THEN 1
                            ELSE 0
                        END DESC,
                        l.log_date DESC
                )
                SELECT
                    COALESCE(microservice_name, 'unknown') AS microservice_name,
                    COALESCE(action_rus, 'unknown') AS action_name,
                    COUNT(*) AS request_count,
                    SUM(CASE
                        WHEN request_status_code >= 400 AND request_status_code < 500 THEN 1
                        ELSE 0
                    END) AS error_count,
                    SUM(CASE WHEN request_status_code >= 500 THEN 1 ELSE 0 END) AS server_error_count,
                    SUM(CASE WHEN duration IS NULL THEN 1 ELSE 0 END) AS unfinished_count,
                    COALESCE(AVG(duration) FILTER (WHERE duration IS NOT NULL), 0) AS avg_duration_ms
                FROM request_last_log
                GROUP BY microservice_name, action_rus
                ORDER BY request_count DESC;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new MethodWindowMetricsDTO(
                        rs.getString("microservice_name"),
                        rs.getString("action_name"),
                        rs.getLong("request_count"),
                        rs.getLong("error_count"),
                        rs.getLong("server_error_count"),
                        rs.getLong("unfinished_count"),
                        rs.getDouble("avg_duration_ms")
                ));
    }
}
