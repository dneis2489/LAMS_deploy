package ru.pstu.lamsv2.repositorys.logRepository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.application.logListFilterDTO.JsonTextFilterDTO;
import ru.pstu.lamsv2.dto.getDataInDB.logDTO.LogListDTO;
import ru.pstu.lamsv2.dto.application.logListFilterDTO.FilterDTO;
import ru.pstu.lamsv2.dto.application.logListFilterDTO.SmartLogSearchDTO;
import ru.pstu.lamsv2.interfaces.logIntefaces.LogListRepositoryInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
    Репозиторий для реализации методов получения перечня логов. Включает в себя методы:
        1. Получения перечня логов с краткой информацией по каждому. С пагинацией.
        2. Получение перечня логов с краткой информацией по каждому. С пагинацией и фильтрацией.
*/

@Repository
public class LogListRepository implements LogListRepositoryInterface
{
    private static final Map<String, List<String>> JSON_PATHS = Map.of(
            "ip", List.of("{ip}", "{clientIp}", "{remoteIp}", "{request,ip}", "{request,clientIp}", "{headers,x-forwarded-for}", "{request,headers,x-forwarded-for}"),
            "browser", List.of("{browser}", "{client,browser}", "{request,browser}", "{userAgent}", "{user_agent}", "{headers,user-agent}", "{request,headers,user-agent}"),
            "useragent", List.of("{userAgent}", "{user_agent}", "{headers,user-agent}", "{request,userAgent}", "{request,headers,user-agent}"),
            "url", List.of("{url}", "{uri}", "{path}", "{endpoint}", "{request,url}", "{request,uri}", "{request,path}", "{request,endpoint}"),
            "method", List.of("{method}", "{httpMethod}", "{request,method}", "{request,httpMethod}"),
            "device", List.of("{device}", "{client,device}", "{request,device}"),
            "message", List.of("{message}", "{error,message}", "{exception,message}", "{request,message}")
    );

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public LogListRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //Получение перечня логов
    @Override
    public List<LogListDTO> getLogs(int page, int pageSize)
    {
        String sql = """
                    SELECT 
                        l.id, 
                        ms.microservice_name, 
                        a.action_rus,
                        l.username,
                        COALESCE(rs_finish.request_status_code, rs.request_status_code) AS request_status_code,
                        rs.request_status_code AS start_status_code,
                        rs_finish.request_status_code AS finish_status_code,
                        l.log_date, 
                        lt.log_type_name 
                    FROM public.logs l 
                    LEFT JOIN public.logs finish_log ON finish_log.correlation_id = l.correlation_id
                        AND finish_log.log_type_id = 2
                    LEFT JOIN public.microservices ms ON l.microservice_id = ms.id 
                    LEFT JOIN public.action_methods a ON l.action_method_id = a.id 
                    LEFT JOIN public.request_status rs ON l.request_status_id = rs.id 
                    LEFT JOIN public.request_status rs_finish ON finish_log.request_status_id = rs_finish.id
                    LEFT JOIN public.log_types lt ON l.log_type_id = lt.id 
                    WHERE l.log_type_id = 1 
                    ORDER BY l.log_date DESC LIMIT :limit OFFSET :offset 
                """;

        int offset = (page - 1) * pageSize;
        Map<String, Object> params = new HashMap<>();
        params.put("limit", pageSize);
        params.put("offset", offset);

        return namedParameterJdbcTemplate.query(sql,  params, (rs, rowNum) ->
                mapLogList(rs));
    }

    //Получение перечня логов с учетом фильтров
    @Override
    public List<LogListDTO> getLogsByFilter(int page, int pageSize, FilterDTO filter)
    {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder ("""
                    SELECT 
                        l.id, 
                        ms.microservice_name, 
                        a.action_rus,
                        l.username, 
                        COALESCE(rs_finish.request_status_code, rs.request_status_code) AS request_status_code,
                        rs.request_status_code AS start_status_code,
                        rs_finish.request_status_code AS finish_status_code,
                        l.log_date, 
                        lt.log_type_name 
                    FROM public.logs l 
                    LEFT JOIN public.logs finish_log ON finish_log.correlation_id = l.correlation_id
                        AND finish_log.log_type_id = 2
                    LEFT JOIN public.microservices ms ON l.microservice_id = ms.id 
                    LEFT JOIN public.action_methods a ON l.action_method_id = a.id 
                    LEFT JOIN public.request_status rs ON l.request_status_id = rs.id 
                    LEFT JOIN public.request_status rs_finish ON finish_log.request_status_id = rs_finish.id
                    LEFT JOIN public.log_types lt ON l.log_type_id = lt.id 
                    WHERE l.log_type_id = 1 
                """);

        //Фильтр по микросервису
        if (filter.hasMicroservice())
        {
            sql.append("AND l.microservice_id IN (:micros) ");
            params.put("micros", filter.getMicroservice());
        }

        //Фильтр по совершенному действию
        if (filter.hasAction())
        {
            sql.append("AND l.action_method_id IN (:action) ");
            params.put("action", filter.getAction());
        }


        //Фильтр по статусу
        if (filter.hasRequestStatus())
        {
            sql.append("AND (l.request_status_id IN (:request_status) OR finish_log.request_status_id IN (:request_status)) ");
            params.put("request_status", filter.getRequestStatus());
        }

        if (filter.isWithoutResponse())
        {
            sql.append("AND finish_log.id IS NULL ");
        }

        //Фильтр по дате начала
        if (filter.hasStartDate())
        {
            sql.append("AND l.log_date >= :startData ");
            params.put("startData", filter.getStartDate());
        }

        //Фильтр по дате окончания
        if (filter.hasEndDate())
        {
            sql.append("AND l.log_date <= :endData ");
            params.put("endData", filter.getEndDate());
        }

        //Пагинация
        int offset = (page - 1) * pageSize;
        sql.append("ORDER BY l.log_date DESC LIMIT :limit OFFSET :offset");
        params.put("limit", pageSize);
        params.put("offset", offset);

        return namedParameterJdbcTemplate.query(sql.toString(),  params, (rs, rowNum) ->
                mapLogList(rs));
    }

    @Override
    public List<LogListDTO> getLogsBySmartSearch(int page, int pageSize, SmartLogSearchDTO search)
    {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder ("""
                    SELECT 
                        l.id, 
                        ms.microservice_name, 
                        a.action_rus,
                        l.username, 
                        COALESCE(rs_finish.request_status_code, rs.request_status_code) AS request_status_code,
                        rs.request_status_code AS start_status_code,
                        rs_finish.request_status_code AS finish_status_code,
                        l.log_date, 
                        lt.log_type_name 
                    FROM public.logs l 
                    LEFT JOIN public.logs finish_log ON finish_log.correlation_id = l.correlation_id
                        AND finish_log.log_type_id = 2
                    LEFT JOIN public.microservices ms ON l.microservice_id = ms.id 
                    LEFT JOIN public.action_methods a ON l.action_method_id = a.id 
                    LEFT JOIN public.request_status rs ON l.request_status_id = rs.id 
                    LEFT JOIN public.request_status rs_finish ON finish_log.request_status_id = rs_finish.id
                    LEFT JOIN public.log_types lt ON l.log_type_id = lt.id 
                    WHERE l.log_type_id = 1 
                """);

        if (search.hasUsername())
        {
            sql.append("AND LOWER(l.username) LIKE :username ");
            params.put("username", like(search.getUsername()));
        }

        if (search.hasActionText())
        {
            sql.append("AND (LOWER(a.action_rus) LIKE :action_text OR LOWER(a.action_eng) LIKE :action_text) ");
            params.put("action_text", like(search.getActionText()));
        }

        if (search.hasMicroserviceText())
        {
            sql.append("AND LOWER(ms.microservice_name) LIKE :microservice_text ");
            params.put("microservice_text", like(search.getMicroserviceText()));
        }

        if (search.hasRequestStatus())
        {
            sql.append("AND (rs.request_status_code ")
                    .append(sqlOperator(search.getRequestStatusOperator()))
                    .append(" :request_status_code OR rs_finish.request_status_code ")
                    .append(sqlOperator(search.getRequestStatusOperator()))
                    .append(" :request_status_code) ");
            params.put("request_status_code", search.getRequestStatus());
        }

        if (search.hasDuration())
        {
            sql.append("AND l.duration ")
                    .append(sqlOperator(search.getDurationOperator()))
                    .append(" :duration ");
            params.put("duration", search.getDuration());
        }

        if (search.hasStartDate())
        {
            sql.append("AND l.log_date >= :smart_start_date ");
            params.put("smart_start_date", search.getStartDate());
        }

        if (search.hasEndDate())
        {
            sql.append("AND l.log_date < :smart_end_date ");
            params.put("smart_end_date", search.getEndDate());
        }

        if (search.hasJsonFilters())
        {
            int index = 0;
            for (JsonTextFilterDTO filter : search.getJsonFilters())
            {
                appendJsonCondition(sql, params, filter, index);
                index++;
            }
        }

        if (search.hasGeneralText())
        {
            sql.append("""
                    AND (
                        LOWER(COALESCE(l.username, '')) LIKE :general_text
                        OR LOWER(COALESCE(ms.microservice_name, '')) LIKE :general_text
                        OR LOWER(COALESCE(a.action_rus, '')) LIKE :general_text
                        OR LOWER(COALESCE(a.action_eng, '')) LIKE :general_text
                        OR LOWER(COALESCE(lt.log_type_name, '')) LIKE :general_text
                        OR LOWER(COALESCE(CAST(l.log_data AS TEXT), '')) LIKE :general_text
                        OR CAST(rs.request_status_code AS TEXT) LIKE :general_text
                        OR CAST(rs_finish.request_status_code AS TEXT) LIKE :general_text
                    ) 
                    """);
            params.put("general_text", like(search.getGeneralText()));
        }

        int offset = (page - 1) * pageSize;
        sql.append("ORDER BY l.log_date DESC LIMIT :limit OFFSET :offset");
        params.put("limit", pageSize);
        params.put("offset", offset);

        return namedParameterJdbcTemplate.query(sql.toString(),  params, (rs, rowNum) ->
                mapLogList(rs));
    }

    private LogListDTO mapLogList(java.sql.ResultSet rs) throws java.sql.SQLException
    {
        return new LogListDTO(
                rs.getLong("id"),
                rs.getString("microservice_name"),
                rs.getString("action_rus"),
                rs.getString("username"),
                rs.getObject("request_status_code", Integer.class),
                rs.getObject("start_status_code", Integer.class),
                rs.getObject("finish_status_code", Integer.class),
                rs.getTimestamp("log_date").toLocalDateTime(),
                rs.getString("log_type_name")
        );
    }
    private String like(String value)
    {
        return "%" + value.toLowerCase().trim() + "%";
    }

    private void appendJsonCondition(StringBuilder sql, Map<String, Object> params, JsonTextFilterDTO filter, int index)
    {
        if (filter == null || filter.getValue() == null || filter.getValue().isBlank())
        {
            return;
        }

        String valueParam = "json_value_" + index;
        String key = filter.getKey() == null ? "" : filter.getKey().replace("_", "").toLowerCase();
        List<String> paths = JSON_PATHS.get(key);
        String operator = normalizeTextOperator(filter.getOperator());
        Object value = "equals".equals(operator) || "notEquals".equals(operator)
                ? filter.getValue().toLowerCase().trim()
                : like(filter.getValue());

        params.put(valueParam, value);

        if (paths == null || paths.isEmpty())
        {
            sql.append("AND LOWER(CAST(l.log_data AS TEXT)) ")
                    .append(textSqlOperator(operator))
                    .append(" :")
                    .append(valueParam)
                    .append(" ");
            return;
        }

        sql.append("AND (");
        for (int i = 0; i < paths.size(); i++)
        {
            if (i > 0)
            {
                sql.append(" OR ");
            }
            sql.append("LOWER(COALESCE(l.log_data #>> '")
                    .append(paths.get(i))
                    .append("', '')) ")
                    .append(textSqlOperator(operator))
                    .append(" :")
                    .append(valueParam);
        }
        sql.append(") ");
    }

    private String sqlOperator(String operator)
    {
        if (operator == null)
        {
            return "=";
        }

        return switch (operator)
        {
            case "notEquals" -> "<>";
            case "gt" -> ">";
            case "gte" -> ">=";
            case "lt" -> "<";
            case "lte" -> "<=";
            default -> "=";
        };
    }

    private String textSqlOperator(String operator)
    {
        return switch (operator)
        {
            case "equals" -> "=";
            case "notEquals" -> "<>";
            default -> "LIKE";
        };
    }

    private String normalizeTextOperator(String operator)
    {
        if ("equals".equals(operator) || "notEquals".equals(operator))
        {
            return operator;
        }

        return "contains";
    }
}
