package ru.pstu.lamsv2.dto.application.riskDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MethodWindowMetricsDTO
{
    private String microserviceName;

    private String actionName;

    private long requestCount;

    private long errorCount;

    private long serverErrorCount;

    private long unfinishedCount;

    private double avgDurationMs;
}
