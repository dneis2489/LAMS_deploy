package ru.pstu.lamsv2.dto.application.riskDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DegradationSourceDTO
{
    private String microserviceName;

    private String actionName;

    private long requestCount;

    private double errorRateDelta;

    private double durationGrowthRate;

    private double trafficImpactRate;

    private double unfinishedRateDelta;

    private double degradationScore;

    private double contributionPercent;

    private String reason;
}
