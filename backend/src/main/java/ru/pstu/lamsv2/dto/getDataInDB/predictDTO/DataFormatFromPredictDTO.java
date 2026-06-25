package ru.pstu.lamsv2.dto.getDataInDB.predictDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 DTO для записи результата прогноза
*/

@Getter
@Setter
@AllArgsConstructor
public class DataFormatFromPredictDTO
{
    private LocalDateTime date;

    private double data;
}
