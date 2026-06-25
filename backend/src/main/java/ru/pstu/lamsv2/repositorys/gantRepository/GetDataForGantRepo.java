package ru.pstu.lamsv2.repositorys.gantRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.gantDTO.UserActivityRequestDTO;
import ru.pstu.lamsv2.interfaces.gantInterface.UserActivityGantRepoInterface;

import java.util.List;

/**
    Репозиторий для реализации методов получения данных для диаграмы Ганта активности пользователей. Содержит методы:
*/

@Repository
public class GetDataForGantRepo implements UserActivityGantRepoInterface
{

    private final JdbcTemplate jdbcTemplate;

    public GetDataForGantRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Получить данные по активности пользователей
    @Override
    public List<UserActivityRequestDTO> getUserActivityForGant()
    {
        String sql = """
                SELECT
                    s.username,
                    s.log_date AS start_date,
                    e.log_date AS end_date,
                    EXTRACT(EPOCH FROM (e.log_date - s.log_date))*1000 AS duration_ms,
                	ms.microservice_name,
                	a.action_rus
                FROM public.logs s
                JOIN public.logs e ON s.correlation_id = e.correlation_id
                LEFT JOIN public.microservices ms ON s.microservice_id = ms.id
                LEFT JOIN public.action_methods a ON s.action_method_id = a.id
                WHERE s.log_type_id = 1
                  AND e.log_type_id = 2
                  AND s.log_date >= CURRENT_TIMESTAMP - INTERVAL '30 days'
                ORDER BY s.log_date DESC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new UserActivityRequestDTO(
                rs.getString("username"),
                rs.getTimestamp("start_date").toLocalDateTime(),
                rs.getTimestamp("end_date").toLocalDateTime(),
                rs.getDouble("duration_ms"),
                rs.getString("microservice_name"),
                rs.getString("action_rus")
        ));
    }
}
