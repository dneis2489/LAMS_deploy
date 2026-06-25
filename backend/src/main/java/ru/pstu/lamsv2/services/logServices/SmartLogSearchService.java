package ru.pstu.lamsv2.services.logServices;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.application.logListFilterDTO.SmartLogSearchDTO;

@Service
public class SmartLogSearchService
{
    private final SmartLogSearchAiClient aiClient;
    private final SmartLogSearchParser parser;

    public SmartLogSearchService(SmartLogSearchAiClient aiClient, SmartLogSearchParser parser)
    {
        this.aiClient = aiClient;
        this.parser = parser;
    }

    public SmartLogSearchDTO parse(String query)
    {
        return aiClient.parse(query).orElseGet(() -> parser.parse(query));
    }
}
