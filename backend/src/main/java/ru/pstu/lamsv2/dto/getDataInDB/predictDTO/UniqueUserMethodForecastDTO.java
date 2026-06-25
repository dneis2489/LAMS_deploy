package ru.pstu.lamsv2.dto.getDataInDB.predictDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UniqueUserMethodForecastDTO
{
    private Long microserviceId;

    private Long actionMethodId;

    private List<DataFormatFromPredictDTO> forecastList;
}
