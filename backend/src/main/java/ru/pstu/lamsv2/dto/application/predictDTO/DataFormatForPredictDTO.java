package ru.pstu.lamsv2.dto.application.predictDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
    DTO для записи данных передаваемых в методы прогнозирования.
*/

@Getter
@Setter
@AllArgsConstructor
public class DataFormatForPredictDTO
{
    private LocalDateTime date;

    private double data;

    private double prediction;
}
