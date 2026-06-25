package ru.pstu.lamsv2.services.riskServices;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.application.riskDTO.DegradationSourceDTO;
import ru.pstu.lamsv2.dto.application.riskDTO.MethodRiskDTO;
import ru.pstu.lamsv2.dto.application.riskDTO.MethodWindowMetricsDTO;
import ru.pstu.lamsv2.repositorys.riskRepository.MethodRiskRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MethodRiskService
{
    private static final double PROBLEM_RATE_THRESHOLD = 0.10;
    private static final double ERROR_WEIGHT = 0.533;
    private static final double DURATION_WEIGHT = 0.267;
    private static final double TRAFFIC_WEIGHT = 0.133;
    private static final double UNFINISHED_WEIGHT = 0.067;

    private final MethodRiskRepository methodRiskRepository;

    public MethodRiskService(MethodRiskRepository methodRiskRepository)
    {
        this.methodRiskRepository = methodRiskRepository;
    }

    public List<MethodRiskDTO> getMethodRisks(String period, int limit)
    {
        Windows windows = buildWindows(period);
        Map<String, MethodWindowMetricsDTO> baselineByMethod = toMap(
                methodRiskRepository.getMethodMetrics(windows.baselineStart(), windows.currentStart())
        );

        return methodRiskRepository.getMethodMetrics(windows.currentStart(), windows.currentEnd()).stream()
                .map(current -> toRisk(current, baselineByMethod.get(key(current))))
                .sorted(Comparator.comparingDouble(MethodRiskDTO::getRiskScore).reversed())
                .limit(normalizeLimit(limit))
                .toList();
    }

    public List<DegradationSourceDTO> getDegradationSources(String period, int limit)
    {
        Windows windows = buildWindows(period);
        Map<String, MethodWindowMetricsDTO> baselineByMethod = toMap(
                methodRiskRepository.getMethodMetrics(windows.baselineStart(), windows.currentStart())
        );

        List<DegradationSourceDTO> rows = methodRiskRepository.getMethodMetrics(windows.currentStart(), windows.currentEnd()).stream()
                .map(current -> toDegradationSource(current, baselineByMethod.get(key(current))))
                .filter(row -> row.getDegradationScore() > 0)
                .sorted(Comparator.comparingDouble(DegradationSourceDTO::getDegradationScore).reversed())
                .toList();

        double totalScore = rows.stream()
                .mapToDouble(DegradationSourceDTO::getDegradationScore)
                .sum();

        if (totalScore <= 0)
        {
            return rows;
        }

        return rows.stream()
                .map(row -> new DegradationSourceDTO(
                        row.getMicroserviceName(),
                        row.getActionName(),
                        row.getRequestCount(),
                        row.getErrorRateDelta(),
                        row.getDurationGrowthRate(),
                        row.getTrafficImpactRate(),
                        row.getUnfinishedRateDelta(),
                        row.getDegradationScore(),
                        round(row.getDegradationScore() / totalScore * 100),
                        row.getReason()
                ))
                .limit(normalizeLimit(limit))
                .toList();
    }

    private MethodRiskDTO toRisk(MethodWindowMetricsDTO current, MethodWindowMetricsDTO baseline)
    {
        double errorRate = rate(
                current.getErrorCount() + current.getServerErrorCount(),
                current.getRequestCount()
        );
        double unfinishedRate = rate(current.getUnfinishedCount(), current.getRequestCount());
        double baselineErrorRate = baseline == null ? 0 : rate(
                baseline.getErrorCount() + baseline.getServerErrorCount(),
                baseline.getRequestCount()
        );
        double baselineAvgDuration = baseline == null ? 0 : baseline.getAvgDurationMs();
        double baselineRequestCount = baseline == null ? 0 : baseline.getRequestCount();

        double durationGrowth = growth(current.getAvgDurationMs(), baselineAvgDuration);
        double trafficDeviation = deviation(current.getRequestCount(), baselineRequestCount);
        double trafficErrorInteraction = trafficErrorInteraction(
                trafficDeviation,
                errorRate,
                unfinishedRate
        );

        double riskScore = 100 * (
                0.54 * errorRate
                        + 0.27 * durationGrowth
                        + 0.13 * trafficErrorInteraction
                        + 0.06 * unfinishedRate
        );
        riskScore = Math.min(100, riskScore);

        return new MethodRiskDTO(
                current.getMicroserviceName(),
                current.getActionName(),
                current.getRequestCount(),
                current.getErrorCount(),
                current.getServerErrorCount(),
                current.getUnfinishedCount(),
                round(current.getAvgDurationMs()),
                round(baselineRequestCount),
                round(baselineErrorRate),
                round(baselineAvgDuration),
                round(errorRate),
                round(durationGrowth),
                round(trafficDeviation),
                round(riskScore),
                riskLevel(riskScore),
                mainRiskReason(errorRate, durationGrowth, trafficErrorInteraction, unfinishedRate)
        );
    }

    private DegradationSourceDTO toDegradationSource(MethodWindowMetricsDTO current, MethodWindowMetricsDTO baseline)
    {
        double baselineDuration = baseline == null ? 0 : baseline.getAvgDurationMs();
        double baselineRequests = baseline == null ? 0 : baseline.getRequestCount();

        double currentErrorRate = rate(
                current.getErrorCount() + current.getServerErrorCount(),
                current.getRequestCount()
        );
        double baselineErrorRate = baseline == null ? 0 : rate(
                baseline.getErrorCount() + baseline.getServerErrorCount(),
                baseline.getRequestCount()
        );
        double errorRateDelta = Math.max(0, currentErrorRate - baselineErrorRate);
        double durationGrowthRate = growth(current.getAvgDurationMs(), baselineDuration);
        double trafficDeviationRate = Math.min(1, deviation(current.getRequestCount(), baselineRequests));
        double currentUnfinishedRate = rate(current.getUnfinishedCount(), current.getRequestCount());
        double baselineUnfinishedRate = baseline == null ? 0 : rate(
                baseline.getUnfinishedCount(),
                baseline.getRequestCount()
        );
        double unfinishedRateDelta = Math.max(0, currentUnfinishedRate - baselineUnfinishedRate);
        double failureFactor = Math.min(
                1,
                (currentErrorRate + currentUnfinishedRate) / PROBLEM_RATE_THRESHOLD
        );
        double trafficImpactRate = trafficDeviationRate * failureFactor;

        double weightedErrorImpact = ERROR_WEIGHT * errorRateDelta;
        double weightedDurationImpact = DURATION_WEIGHT * durationGrowthRate;
        double weightedTrafficImpact = TRAFFIC_WEIGHT * trafficImpactRate;
        double weightedUnfinishedImpact = UNFINISHED_WEIGHT * unfinishedRateDelta;
        double degradationScore = 100 * Math.min(
                1,
                weightedErrorImpact
                        + weightedDurationImpact
                        + weightedTrafficImpact
                        + weightedUnfinishedImpact
        );

        return new DegradationSourceDTO(
                current.getMicroserviceName(),
                current.getActionName(),
                current.getRequestCount(),
                round(errorRateDelta),
                round(durationGrowthRate),
                round(trafficImpactRate),
                round(unfinishedRateDelta),
                round(degradationScore),
                0,
                mainDegradationReason(
                        weightedErrorImpact,
                        weightedDurationImpact,
                        weightedTrafficImpact,
                        weightedUnfinishedImpact
                )
        );
    }

    private Windows buildWindows(String period)
    {
        Duration window = switch (period == null ? "day" : period.toLowerCase())
        {
            case "hour" -> Duration.ofHours(1);
            case "month" -> Duration.ofDays(30);
            default -> Duration.ofDays(1);
        };

        LocalDateTime currentEnd = LocalDateTime.now();
        LocalDateTime currentStart = currentEnd.minus(window);
        LocalDateTime baselineStart = currentStart.minus(window);

        return new Windows(baselineStart, currentStart, currentEnd);
    }

    private Map<String, MethodWindowMetricsDTO> toMap(List<MethodWindowMetricsDTO> rows)
    {
        Map<String, MethodWindowMetricsDTO> result = new HashMap<>();
        for (MethodWindowMetricsDTO row : rows)
        {
            result.put(key(row), row);
        }
        return result;
    }

    private String key(MethodWindowMetricsDTO row)
    {
        return row.getMicroserviceName() + "\u0000" + row.getActionName();
    }

    private double rate(double value, double total)
    {
        return total <= 0 ? 0 : value / total;
    }

    private double growth(double current, double baseline)
    {
        if (baseline <= 0)
        {
            return current > 0 ? 1 : 0;
        }
        return Math.max(0, (current - baseline) / baseline);
    }

    private double deviation(double current, double baseline)
    {
        if (baseline <= 0)
        {
            return current > 0 ? 1 : 0;
        }
        return Math.abs(current - baseline) / baseline;
    }

    private double trafficErrorInteraction(
            double trafficDeviation,
            double errorRate,
            double unfinishedRate
    )
    {
        double failureRate = errorRate + unfinishedRate;
        double failureFactor = Math.min(1, failureRate / 0.10);
        return Math.min(1, trafficDeviation) * failureFactor;
    }

    private int normalizeLimit(int limit)
    {
        if (limit <= 0)
        {
            return 10;
        }
        return Math.min(limit, 50);
    }

    private String riskLevel(double riskScore)
    {
        if (riskScore >= 75)
        {
            return "CRITICAL";
        }
        if (riskScore >= 50)
        {
            return "HIGH";
        }
        if (riskScore >= 25)
        {
            return "MEDIUM";
        }
        return "LOW";
    }

    private String mainRiskReason(double errorRate, double durationGrowth, double trafficErrorInteraction, double unfinishedRate)
    {
        double weightedErrorRate = 0.54 * errorRate;
        double weightedDurationGrowth = 0.27 * durationGrowth;
        double weightedTrafficInteraction = 0.13 * trafficErrorInteraction;
        double weightedUnfinishedRate = 0.06 * unfinishedRate;
        double max = Math.max(
                Math.max(weightedErrorRate, weightedDurationGrowth),
                Math.max(weightedTrafficInteraction, weightedUnfinishedRate)
        );
        if (max == weightedDurationGrowth)
        {
            return "LATENCY_GROWTH";
        }
        if (max == weightedTrafficInteraction)
        {
            return "TRAFFIC_DEVIATION";
        }
        if (max == weightedUnfinishedRate)
        {
            return "UNFINISHED_REQUESTS";
        }
        return "ERROR_RATE";
    }

    private String mainDegradationReason(
            double weightedErrorImpact,
            double weightedDurationImpact,
            double weightedTrafficImpact,
            double weightedUnfinishedImpact
    )
    {
        double max = Math.max(
                Math.max(weightedErrorImpact, weightedDurationImpact),
                Math.max(weightedTrafficImpact, weightedUnfinishedImpact)
        );
        if (max == weightedDurationImpact)
        {
            return "LATENCY_GROWTH";
        }
        if (max == weightedTrafficImpact)
        {
            return "TRAFFIC_CHANGE";
        }
        if (max == weightedUnfinishedImpact)
        {
            return "UNFINISHED_REQUESTS";
        }
        return "ERROR_GROWTH";
    }

    private double round(double value)
    {
        return Math.round(value * 100.0) / 100.0;
    }

    private record Windows(LocalDateTime baselineStart, LocalDateTime currentStart, LocalDateTime currentEnd)
    {
    }
}
