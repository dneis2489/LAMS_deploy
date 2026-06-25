package ru.pstu.lamsv2.services.logServices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.pstu.lamsv2.dto.application.logListFilterDTO.SmartLogSearchDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class SmartLogSearchAiClient
{
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final boolean enabled;
    private final String baseUrl;
    private final String apiKey;
    private final String model;
    private final boolean responseFormatEnabled;

    public SmartLogSearchAiClient(
            @Value("${lams.ai.smart-search.enabled:false}") boolean enabled,
            @Value("${lams.ai.smart-search.base-url:http://localhost:11434/v1}") String baseUrl,
            @Value("${lams.ai.smart-search.api-key:ollama}") String apiKey,
            @Value("${lams.ai.smart-search.model:qwen2.5:7b}") String model,
            @Value("${lams.ai.smart-search.response-format-enabled:false}") boolean responseFormatEnabled
    )
    {
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
        this.enabled = enabled;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.responseFormatEnabled = responseFormatEnabled;
    }

    public Optional<SmartLogSearchDTO> parse(String query)
    {
        if (!enabled || apiKey == null || apiKey.isBlank() || query == null || query.isBlank())
        {
            return Optional.empty();
        }

        try
        {
            Map<String, Object> requestPayload = new java.util.HashMap<>();
            requestPayload.put("model", model);
            requestPayload.put("temperature", 0);
            requestPayload.put("messages", List.of(
                    Map.of(
                            "role", "system",
                            "content", systemPrompt()
                    ),
                    Map.of(
                            "role", "user",
                            "content", query
                    )
            ));

            if (responseFormatEnabled)
            {
                requestPayload.put("response_format", Map.of("type", "json_object"));
            }

            String requestBody = objectMapper.writeValueAsString(requestPayload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(normalizeBaseUrl(baseUrl) + "/chat/completions"))
                    .timeout(Duration.ofSeconds(20))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300)
            {
                return Optional.empty();
            }

            JsonNode root = objectMapper.readTree(response.body());
            String content = root.path("choices").path(0).path("message").path("content").asText("");
            if (content.isBlank())
            {
                return Optional.empty();
            }

            return Optional.of(objectMapper.readValue(extractJson(content), SmartLogSearchDTO.class));
        }
        catch (Exception ignored)
        {
            return Optional.empty();
        }
    }

    private String extractJson(String content)
    {
        String trimmed = content.trim();
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');

        if (start >= 0 && end > start)
        {
            return trimmed.substring(start, end + 1);
        }

        return trimmed;
    }

    private String normalizeBaseUrl(String value)
    {
        if (value.endsWith("/"))
        {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }

    private String systemPrompt()
    {
        return """
                You convert Russian or English log search requests into strict JSON.
                Return only one JSON object. Do not use markdown. Do not add explanations.
                The object must be compatible with these fields:
                {
                  "username": string | null,
                  "actionText": string | null,
                  "microserviceText": string | null,
                  "requestStatus": number | null,
                  "requestStatusOperator": "equals" | "notEquals" | "gt" | "gte" | "lt" | "lte",
                  "duration": number | null,
                  "durationOperator": "equals" | "gt" | "gte" | "lt" | "lte",
                  "startDate": "YYYY-MM-DD" | null,
                  "endDate": "YYYY-MM-DD" | null,
                  "generalText": string | null,
                  "jsonFilters": [
                    {
                      "key": "ip" | "browser" | "userAgent" | "url" | "method" | "device" | "message",
                      "operator": "equals" | "contains" | "notEquals",
                      "value": string
                    }
                  ]
                }
                Use duration in milliseconds. Use endDate as exclusive next day for single-day requests.
                Do not invent values. If a part is unclear, put it into generalText.
                """;
    }
}
