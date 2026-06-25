package ru.pstu.lamsv2.services.logServices;

import org.springframework.stereotype.Component;
import ru.pstu.lamsv2.dto.application.logListFilterDTO.JsonTextFilterDTO;
import ru.pstu.lamsv2.dto.application.logListFilterDTO.SmartLogSearchDTO;

import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SmartLogSearchParser
{
    private static final Pattern IP_PATTERN = Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");
    private static final Pattern STATUS_PATTERN = Pattern.compile("(?:status|\\u0441\\u0442\\u0430\\u0442\\u0443\\u0441|http)\\s*(?:=|:)?\\s*(\\d{3})", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern USER_PATTERN = Pattern.compile("(?:user|username|\\u043f\\u043e\\u043b\\u044c\\u0437\\u043e\\u0432\\u0430\\u0442\\u0435\\u043b\\u044c|\\u044e\\u0437\\u0435\\u0440|\\u0438\\u043d\\u0438\\u0446\\u0438\\u0430\\u0442\\u043e\\u0440)\\s*(?:=|:)?\\s*([\\p{L}\\p{N}_.@-]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern ACTION_PATTERN = Pattern.compile("(?:action|method|\\u0434\\u0435\\u0439\\u0441\\u0442\\u0432\\u0438\\u0435|\\u043c\\u0435\\u0442\\u043e\\u0434)\\s*(?:=|:)?\\s*([\\p{L}\\p{N}_ ./-]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern MICROSERVICE_PATTERN = Pattern.compile("(?:microservice|service|\\u043c\\u0438\\u043a\\u0440\\u043e\\u0441\\u0435\\u0440\\u0432\\u0438\\u0441|\\u0441\\u0435\\u0440\\u0432\\u0438\\u0441)\\s*(?:=|:)?\\s*([\\p{L}\\p{N}_.-]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern URL_PATTERN = Pattern.compile("(?:url|uri|endpoint|\\u0430\\u0434\\u0440\\u0435\\u0441|\\u043f\\u0443\\u0442\\u044c)\\s*(?:\\u0441\\u043e\\u0434\\u0435\\u0440\\u0436\\u0438\\u0442|=|:)?\\s*([^\\s,;]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern USER_AGENT_PATTERN = Pattern.compile("(?:user[- ]?agent|ua)\\s*(?:=|:)?\\s*([^,;]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern DURATION_PATTERN = Pattern.compile("(?:duration|\\u0434\\u043b\\u0438\\u0442\\u0435\\u043b\\u044c\\u043d\\u043e\\u0441\\u0442\\u044c|\\u0432\\u0440\\u0435\\u043c\\u044f)\\s*(>|<|>=|<=|=)?\\s*(\\d+)\\s*(ms|\\u043c\\u0441|s|sec|\\u0441\\u0435\\u043a|\\u0441\\u0435\\u043a\\u0443\\u043d\\u0434)?", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern PATH_PATTERN = Pattern.compile("(/[^\\s,;]+)");

    private static final String[] BROWSERS = {
            "chrome", "firefox", "safari", "edge", "opera", "yandex", "yabrowser", "chromium"
    };

    public SmartLogSearchDTO parse(String query)
    {
        SmartLogSearchDTO search = new SmartLogSearchDTO();

        if (query == null || query.isBlank())
        {
            return search;
        }

        String normalized = query.trim();
        String lower = normalized.toLowerCase(Locale.ROOT);

        addIpFilter(search, normalized);
        addBrowserFilter(search, lower);
        addUrlFilter(search, normalized);
        addUserAgentFilter(search, normalized);
        addStatus(search, normalized);
        addDuration(search, normalized);
        addDateRange(search, lower);
        addUsername(search, normalized);
        addAction(search, normalized);
        addMicroservice(search, normalized);

        if (!search.hasUsername()
                && !search.hasActionText()
                && !search.hasMicroserviceText()
                && !search.hasRequestStatus()
                && !search.hasDuration()
                && !search.hasStartDate()
                && !search.hasEndDate()
                && !search.hasJsonFilters())
        {
            search.setGeneralText(normalized);
        }

        return search;
    }

    private void addIpFilter(SmartLogSearchDTO search, String query)
    {
        Matcher matcher = IP_PATTERN.matcher(query);
        if (matcher.find())
        {
            search.getJsonFilters().add(new JsonTextFilterDTO("ip", matcher.group(), "equals"));
        }
    }

    private void addBrowserFilter(SmartLogSearchDTO search, String lowerQuery)
    {
        for (String browser : BROWSERS)
        {
            if (lowerQuery.contains(browser))
            {
                search.getJsonFilters().add(new JsonTextFilterDTO("browser", browser, "contains"));
                return;
            }
        }
    }

    private void addUrlFilter(SmartLogSearchDTO search, String query)
    {
        Matcher matcher = URL_PATTERN.matcher(query);
        if (matcher.find())
        {
            search.getJsonFilters().add(new JsonTextFilterDTO("url", cleanValue(matcher.group(1)), "contains"));
            return;
        }

        matcher = PATH_PATTERN.matcher(query);
        if (matcher.find())
        {
            search.getJsonFilters().add(new JsonTextFilterDTO("url", cleanValue(matcher.group(1)), "contains"));
        }
    }

    private void addUserAgentFilter(SmartLogSearchDTO search, String query)
    {
        Matcher matcher = USER_AGENT_PATTERN.matcher(query);
        if (matcher.find())
        {
            search.getJsonFilters().add(new JsonTextFilterDTO("userAgent", cleanValue(matcher.group(1)), "contains"));
        }
    }

    private void addStatus(SmartLogSearchDTO search, String query)
    {
        Matcher matcher = STATUS_PATTERN.matcher(query);
        if (matcher.find())
        {
            search.setRequestStatus(Integer.parseInt(matcher.group(1)));
        }
    }

    private void addDuration(SmartLogSearchDTO search, String query)
    {
        Matcher matcher = DURATION_PATTERN.matcher(query);
        if (!matcher.find())
        {
            return;
        }

        String operator = matcher.group(1);
        int value = Integer.parseInt(matcher.group(2));
        String unit = matcher.group(3);

        if (unit != null && !unit.equalsIgnoreCase("ms") && !unit.equalsIgnoreCase("\u043c\u0441"))
        {
            value *= 1000;
        }

        search.setDuration(value);
        search.setDurationOperator(toOperator(operator));
    }

    private void addDateRange(SmartLogSearchDTO search, String lowerQuery)
    {
        LocalDate today = LocalDate.now();

        if (lowerQuery.contains("\u0441\u0435\u0433\u043e\u0434\u043d\u044f") || lowerQuery.contains("today"))
        {
            search.setStartDate(today);
            search.setEndDate(today.plusDays(1));
            return;
        }

        if (lowerQuery.contains("\u0432\u0447\u0435\u0440\u0430") || lowerQuery.contains("yesterday"))
        {
            search.setStartDate(today.minusDays(1));
            search.setEndDate(today);
            return;
        }

        if (lowerQuery.contains("\u043d\u0435\u0434\u0435\u043b") || lowerQuery.contains("week"))
        {
            search.setStartDate(today.minusDays(7));
            search.setEndDate(today.plusDays(1));
        }
    }

    private void addUsername(SmartLogSearchDTO search, String query)
    {
        Matcher matcher = USER_PATTERN.matcher(query);
        if (matcher.find())
        {
            search.setUsername(cleanValue(matcher.group(1)));
        }
    }

    private void addAction(SmartLogSearchDTO search, String query)
    {
        Matcher matcher = ACTION_PATTERN.matcher(query);
        if (matcher.find())
        {
            search.setActionText(cleanValue(matcher.group(1)));
        }
    }

    private void addMicroservice(SmartLogSearchDTO search, String query)
    {
        Matcher matcher = MICROSERVICE_PATTERN.matcher(query);
        if (matcher.find())
        {
            search.setMicroserviceText(cleanValue(matcher.group(1)));
        }
    }

    private String cleanValue(String value)
    {
        return value == null ? null : value.trim().replaceAll("[,;]+$", "");
    }

    private String toOperator(String operator)
    {
        if (operator == null || operator.isBlank() || "=".equals(operator))
        {
            return "equals";
        }

        return switch (operator)
        {
            case ">" -> "gt";
            case "<" -> "lt";
            case ">=" -> "gte";
            case "<=" -> "lte";
            default -> "equals";
        };
    }
}
