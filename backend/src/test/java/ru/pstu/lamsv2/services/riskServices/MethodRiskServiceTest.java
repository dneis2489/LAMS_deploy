package ru.pstu.lamsv2.services.riskServices;

import org.junit.jupiter.api.Test;
import ru.pstu.lamsv2.dto.application.riskDTO.DegradationSourceDTO;
import ru.pstu.lamsv2.dto.application.riskDTO.MethodRiskDTO;
import ru.pstu.lamsv2.dto.application.riskDTO.MethodWindowMetricsDTO;
import ru.pstu.lamsv2.repositorys.riskRepository.MethodRiskRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MethodRiskServiceTest
{
    @Test
    void calculatesDegradationUsingRelativeChangesAndSaatyWeights()
    {
        MethodRiskRepository repository = mock(MethodRiskRepository.class);
        MethodRiskService service = new MethodRiskService(repository);
        MethodWindowMetricsDTO baseline = new MethodWindowMetricsDTO(
                "orders", "create", 1000, 15, 5, 10, 200
        );
        MethodWindowMetricsDTO current = new MethodWindowMetricsDTO(
                "orders", "create", 1200, 40, 20, 24, 250
        );

        when(repository.getMethodMetrics(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(baseline))
                .thenReturn(List.of(current));

        List<DegradationSourceDTO> result = service.getDegradationSources("day", 10);

        assertThat(result).hasSize(1);
        DegradationSourceDTO row = result.get(0);
        assertThat(row.getErrorRateDelta()).isEqualTo(0.03);
        assertThat(row.getDurationGrowthRate()).isEqualTo(0.25);
        assertThat(row.getTrafficImpactRate()).isEqualTo(0.14);
        assertThat(row.getUnfinishedRateDelta()).isEqualTo(0.01);
        assertThat(row.getDegradationScore()).isEqualTo(10.2);
        assertThat(row.getContributionPercent()).isEqualTo(100.0);
        assertThat(row.getReason()).isEqualTo("LATENCY_GROWTH");
    }

    @Test
    void calculatesRiskUsingCombinedClientAndServerErrorRate()
    {
        MethodRiskRepository repository = mock(MethodRiskRepository.class);
        MethodRiskService service = new MethodRiskService(repository);
        MethodWindowMetricsDTO baseline = new MethodWindowMetricsDTO(
                "orders", "create", 80, 0, 0, 0, 100
        );
        MethodWindowMetricsDTO current = new MethodWindowMetricsDTO(
                "orders", "create", 100, 10, 5, 2, 120
        );

        when(repository.getMethodMetrics(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(baseline))
                .thenReturn(List.of(current));

        List<MethodRiskDTO> result = service.getMethodRisks("day", 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getErrorRate()).isEqualTo(0.15);
        assertThat(result.get(0).getBaselineErrorRate()).isZero();
        assertThat(result.get(0).getRiskScore()).isEqualTo(16.87);
        assertThat(result.get(0).getMainReason()).isEqualTo("ERROR_RATE");
    }
}
