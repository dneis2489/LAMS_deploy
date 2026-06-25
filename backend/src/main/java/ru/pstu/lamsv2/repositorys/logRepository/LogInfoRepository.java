package ru.pstu.lamsv2.repositorys.logRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.logDTO.FullLogInfoDTO;
import ru.pstu.lamsv2.dto.getDataInDB.logDTO.LogJSONInfoData;
import ru.pstu.lamsv2.interfaces.logIntefaces.LogInfoRepositoryInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
    Репозиторий для реализации методов получения данных для полной информации о логе и связанном с ним логами
*/

@Repository
public class LogInfoRepository implements LogInfoRepositoryInterface
{
    @Autowired
    private ObjectMapper objectMapper;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public LogInfoRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    // Получение полной информации по конкретному логу
    @Override
    public List<FullLogInfoDTO> getLogInfo(long id)
    {
        String sql = """
                    SELECT 
                        l.id,
                        ms.microservice_name,
                        a.action_rus,
                        l.username,
                        rs.request_status_code,
                        l.log_date,
                        lt.log_type_name,
                        l.log_data,
                        l.duration
                    FROM public.logs l
                    LEFT JOIN public.microservices ms ON l.microservice_id = ms.id
                    LEFT JOIN public.action_methods a ON l.action_method_id = a.id
                    LEFT JOIN public.request_status rs ON l.request_status_id = rs.id
                    LEFT JOIN public.log_types lt ON l.log_type_id = lt.id
                    WHERE correlation_id = (
                        SELECT correlation_id FROM public.logs WHERE id = :id
                    )
                    ORDER BY l.log_date DESC
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        return namedParameterJdbcTemplate.query(sql,  params, (rs, rowNum) ->{
            String jsonText = rs.getString("log_data");
            return new FullLogInfoDTO(
                    rs.getLong("id"),
                    rs.getString("microservice_name"),
                    rs.getString("action_rus"),
                    rs.getString("username"),
                    rs.getObject("request_status_code", Integer.class),
                    rs.getTimestamp("log_date").toLocalDateTime(),
                    rs.getString("log_type_name"),
                    parseLogData(jsonText),
                    rs.getInt("duration")
            );
        });
    }

    private LogJSONInfoData parseLogData(String jsonText)
    {
        try
        {
            return objectMapper.readValue(jsonText, LogJSONInfoData.class);
        }
        catch (JsonProcessingException exception)
        {
            throw new IllegalStateException("Failed to parse log_data JSON", exception);
        }
    }
}
