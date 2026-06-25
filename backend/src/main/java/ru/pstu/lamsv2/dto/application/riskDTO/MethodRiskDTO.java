package ru.pstu.lamsv2.dto.application.riskDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MethodRiskDTO
{
    private String microserviceName;

    private String actionName;

    private long requestCount;

    private long errorCount;

    private long serverErrorCount;

    private long unfinishedCount;

    private double avgDurationMs;

    private double baselineRequestCount;

    private double baselineErrorRate;

    private double baselineAvgDurationMs;

    private double errorRate;

    private double durationGrowthRate;

    private double trafficDeviationRate;

    private double riskScore;

    private String riskLevel;

    private String mainReason;
}
