package ru.pstu.lamsv2.repositorys.FilterForClientRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.filterForClientDTO.RequestStatusListForFilterDTO;
import ru.pstu.lamsv2.dto.getDataInDB.filterForClientDTO.MicrosAndActionListForFilterDTO;
import ru.pstu.lamsv2.interfaces.FilterInterface.GetDataForFilterRepoInterface;

import java.util.List;

/**
    Репозиторий для реализации методов получения данных для фильтров. Содержит методы:
        1. Получить список микросервисов и их методов
        2. Получить список статусов ответов у логов
*/

@Repository
public class FilterForClientRepository implements GetDataForFilterRepoInterface
{

    private final JdbcTemplate jdbcTemplate;

    public FilterForClientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Получить список микросервисов и их методов
    @Override
    public List<MicrosAndActionListForFilterDTO> getMicroserviceAndActionToFilter()
    {
        String sql = """
                SELECT
                    am.id,
                    am.microservice_id,
                	ms.microservice_name AS microserviceName,
                    am.action_eng,
                    am.action_rus
                FROM
                    public.action_methods am
                JOIN
                    public.microservices ms ON am.microservice_id = ms.id;
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new MicrosAndActionListForFilterDTO(
                rs.getLong("id"),
                rs.getLong("microservice_id"),
                rs.getString("microserviceName"),
                rs.getString("action_eng"),
                rs.getString("action_rus")
        ));
    }

    //Получить список статусов ответов у логов
    @Override
    public List<RequestStatusListForFilterDTO> getRequestStatusToFilter()
    {
        String sql = """
                SELECT id, request_status_code
                	FROM public.request_status;
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new RequestStatusListForFilterDTO(
                rs.getLong("id"),
                rs.getLong("request_status_code")
        ));
    }
}
