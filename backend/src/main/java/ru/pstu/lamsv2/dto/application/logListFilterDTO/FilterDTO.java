package ru.pstu.lamsv2.dto.application.logListFilterDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 DTO фильтрации логов. Используется в методе получения отфильтрованного списка логов.
*/

@Getter
@Setter
public class FilterDTO
{
    private List<Integer> microservice;

    private List<Integer> action;

    private List<Integer> requestStatus;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean withoutResponse;

    //Вспомогательные методы, если поле пустое, то вернет false
    public boolean hasMicroservice() {return microservice != null; }
    public boolean hasAction() {return action != null; }
    public boolean hasRequestStatus() {return requestStatus != null; }
    public boolean hasStartDate() {return startDate != null; }
    public boolean hasEndDate() {return endDate != null; }
    public boolean isWithoutResponse() {return Boolean.TRUE.equals(withoutResponse); }
}
