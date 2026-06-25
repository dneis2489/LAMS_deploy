package ru.pstu.lamsv2.dto.application.logListFilterDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SmartLogSearchDTO
{
    private String username;

    private String actionText;

    private String microserviceText;

    private Integer requestStatus;

    private String requestStatusOperator = "equals";

    private Integer duration;

    private String durationOperator = "equals";

    private LocalDate startDate;

    private LocalDate endDate;

    private String generalText;

    private List<JsonTextFilterDTO> jsonFilters = new ArrayList<>();

    public boolean hasUsername() { return hasText(username); }
    public boolean hasActionText() { return hasText(actionText); }
    public boolean hasMicroserviceText() { return hasText(microserviceText); }
    public boolean hasRequestStatus() { return requestStatus != null; }
    public boolean hasDuration() { return duration != null; }
    public boolean hasStartDate() { return startDate != null; }
    public boolean hasEndDate() { return endDate != null; }
    public boolean hasGeneralText() { return hasText(generalText); }
    public boolean hasJsonFilters() { return jsonFilters != null && !jsonFilters.isEmpty(); }

    private boolean hasText(String value)
    {
        return value != null && !value.isBlank();
    }
}
