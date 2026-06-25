package ru.pstu.lamsv2.dto.getDataInDB.logDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
    DTO для заполнения JSON части полной информации о логе.
*/

@Getter
@Setter
@AllArgsConstructor
public class LogJSONInfoData
{
    private String phase;

    private String message;

    private Integer method_id;

    private Integer service_id;
}
