package ru.pstu.lamsv2.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pstu.lamsv2.dto.application.riskDTO.DegradationSourceDTO;
import ru.pstu.lamsv2.dto.application.riskDTO.MethodRiskDTO;
import ru.pstu.lamsv2.services.riskServices.MethodRiskService;

import java.util.List;

@RestController
@RequestMapping("lams")
public class MethodRiskController
{
    private final MethodRiskService methodRiskService;

    public MethodRiskController(MethodRiskService methodRiskService)
    {
        this.methodRiskService = methodRiskService;
    }

    @GetMapping("/method-risk")
    public List<MethodRiskDTO> getMethodRisks(
            @RequestParam(defaultValue = "day") String period,
            @RequestParam(defaultValue = "10") int limit
    )
    {
        return methodRiskService.getMethodRisks(period, limit);
    }

    @GetMapping("/degradation-sources")
    public List<DegradationSourceDTO> getDegradationSources(
            @RequestParam(defaultValue = "day") String period,
            @RequestParam(defaultValue = "10") int limit
    )
    {
        return methodRiskService.getDegradationSources(period, limit);
    }
}
