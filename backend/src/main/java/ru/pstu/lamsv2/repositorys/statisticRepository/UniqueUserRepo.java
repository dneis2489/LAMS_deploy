package ru.pstu.lamsv2.repositorys.statisticRepository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.UniqueUsersForMethodStatDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.totalStat.UniqueUsersStatDTO;
import ru.pstu.lamsv2.interfaces.statisticIntefaces.StatGetUniqueUserRepoInterface;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
    Репозиторий реализующий методы получения данных для статистики по уникальным пользователям. Содержит методы:
        1. Получение перечня и количества уникальных пользователей системы по методам микросервисов с агрегацией по часам/дням/месяца
        2. Получение перечня и количества уникальных пользователей системы с агрегацией по часам/дням/месяцам
*/

@Repository
public class UniqueUserRepo implements StatGetUniqueUserRepoInterface
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UniqueUserRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //Получение количества уникальных пользователей по методам микросервисов с агрегацией по часам
    @Override
    public List<UniqueUsersForMethodStatDTO> getUniqueUserForMethodsWithHour(int hour)
    {
        LocalDateTime sinceDateTime = LocalDateTime.now().minusHours(hour);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDateTime", sinceDateTime);
        String sql = """
                    SELECT
                         	s.stat_hour,
                         	m.microservice_name,
                         	am.action_rus,
                         	s.unique_users_count,
                    		s.unique_users,
                    		s.predict
                    FROM public.unique_users_stats_method_hour s
                    JOIN public.microservices m ON m.id = s.microservice_id
                    JOIN public.action_methods am ON am.id = s.action_method_id
                    WHERE stat_hour >= :sinceDateTime
                    ORDER BY s.stat_hour DESC, m.microservice_name, am.action_eng;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new UniqueUsersForMethodStatDTO(
                        rs.getTimestamp("stat_hour").toLocalDateTime(),
                        rs.getString("microservice_name"),
                        rs.getString("action_rus"),
                        getNullableLong(rs, "unique_users_count"),
                        rs.getArray("unique_users") == null
                                ? List.of()
                                : Arrays.asList((String[]) rs.getArray("unique_users").getArray()),
                        rs.getLong("predict")
                ));
    }

    //Получение количества уникальных пользователей по методам микросервисов с агрегацией по дням
    @Override
    public List<UniqueUsersForMethodStatDTO> getUniqueUserForMethodsWithDay(int days)
    {
        LocalDateTime sinceDate = LocalDateTime.now().minusMonths(days);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);
        String sql = """
                    SELECT
                         	s.stat_day,
                         	m.microservice_name,
                         	am.action_rus,
                         	s.unique_users_count,
                    		s.unique_users,
                    		s.predict
                    FROM public.unique_users_stats_method_day s
                    JOIN public.microservices m ON m.id = s.microservice_id
                    JOIN public.action_methods am ON am.id = s.action_method_id
                    WHERE stat_day >= :sinceDate
                    ORDER BY s.stat_day DESC, m.microservice_name, am.action_eng;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new UniqueUsersForMethodStatDTO(
                        rs.getTimestamp("stat_day").toLocalDateTime(),
                        rs.getString("microservice_name"),
                        rs.getString("action_rus"),
                        getNullableLong(rs, "unique_users_count"),
                        rs.getArray("unique_users") == null
                                ? List.of()
                                : Arrays.asList((String[]) rs.getArray("unique_users").getArray()),
                        rs.getLong("predict")
                ));
    }

    //Получение количества уникальных пользователей по методам микросервисов с агрегацией по месяцам
    @Override
    public List<UniqueUsersForMethodStatDTO> getUniqueUserForMethodsWithMonth(int month)
    {
        LocalDateTime sinceDate = LocalDateTime.now().minusMonths(month);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);
        String sql = """
                    SELECT
                         	s.stat_month,
                         	m.microservice_name,
                         	am.action_rus,
                         	s.unique_users_count,
                    		s.unique_users,
                    		s.predict
                    FROM public.unique_users_stats_method_month s
                    JOIN public.microservices m ON m.id = s.microservice_id
                    JOIN public.action_methods am ON am.id = s.action_method_id
                    WHERE stat_month >= :sinceDate
                    ORDER BY s.stat_month DESC, m.microservice_name, am.action_eng;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new UniqueUsersForMethodStatDTO(
                        rs.getTimestamp("stat_month").toLocalDateTime(),
                        rs.getString("microservice_name"),
                        rs.getString("action_rus"),
                        getNullableLong(rs, "unique_users_count"),
                        rs.getArray("unique_users") == null
                                ? List.of()
                                : Arrays.asList((String[]) rs.getArray("unique_users").getArray()),
                        rs.getLong("predict")
                ));
    }

    //Получение количества уникальных пользователей с агрегацией по часам
    @Override
    public List<UniqueUsersStatDTO> getUniqueUserWithHour(int hour)
    {
        LocalDateTime sinceDateTime = LocalDateTime.now().minusHours(hour);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDateTime", sinceDateTime);
        String sql = """
                    SELECT id, stat_hour, unique_users_count, predict, unique_users
                    FROM public.unique_users_stats_hour
                    WHERE stat_hour >= :sinceDateTime
                    ORDER BY stat_hour;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new UniqueUsersStatDTO(
                        rs.getLong("id"),
                        rs.getTimestamp("stat_hour").toLocalDateTime(),
                        getNullableLong(rs, "unique_users_count"),
                        rs.getLong("predict"),
                        rs.getArray("unique_users") == null
                                ? List.of()
                                : Arrays.asList((String[]) rs.getArray("unique_users").getArray())
                ));
    }

    //Получение количества уникальных пользователей с агрегацией по дням
    @Override
    public List<UniqueUsersStatDTO> getUniqueUserWithDay(int days)
    {
        LocalDateTime sinceDate = LocalDateTime.now().minusMonths(days);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);
        String sql = """
                    SELECT id, stat_day, unique_users_count, predict, unique_users
                    FROM public.unique_users_stats_day
                    WHERE stat_day >= :sinceDate
                    ORDER BY stat_day;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new UniqueUsersStatDTO(
                        rs.getLong("id"),
                        rs.getTimestamp("stat_day").toLocalDateTime(),
                        getNullableLong(rs, "unique_users_count"),
                        rs.getLong("predict"),
                        rs.getArray("unique_users") == null
                                ? List.of()
                                : Arrays.asList((String[]) rs.getArray("unique_users").getArray())
                ));
    }

    //Получение количества уникальных пользователей с агрегацией по месяцам
    @Override
    public List<UniqueUsersStatDTO> getUniqueUserWithMonth(int month)
    {
        LocalDateTime sinceDate = LocalDateTime.now().minusMonths(month);
        Map<String, Object> params = new HashMap<>();
        params.put("sinceDate", sinceDate);
        String sql = """
                    SELECT id, stat_month, unique_users_count, predict, unique_users
                    FROM public.unique_users_stats_month
                    WHERE stat_month >= :sinceDate
                    ORDER BY stat_month;
                """;

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
                new UniqueUsersStatDTO(
                        rs.getLong("id"),
                        rs.getTimestamp("stat_month").toLocalDateTime(),
                        getNullableLong(rs, "unique_users_count"),
                        rs.getLong("predict"),
                        rs.getArray("unique_users") == null
                                ? List.of()
                                : Arrays.asList((String[]) rs.getArray("unique_users").getArray())
                ));
    }
    private static Long getNullableLong(ResultSet rs, String columnName) throws SQLException
    {
        Object value = rs.getObject(columnName);

        if (value == null)
        {
            return null;
        }

        if (value instanceof Number number)
        {
            return number.longValue();
        }

        return Long.valueOf(value.toString());
    }
}
