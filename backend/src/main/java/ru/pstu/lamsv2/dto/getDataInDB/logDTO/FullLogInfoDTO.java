package ru.pstu.lamsv2.dto.getDataInDB.logDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
    DTO для получения полной информации о логе
*/

@Getter
@Setter
@AllArgsConstructor
public class FullLogInfoDTO
{
    private long id;

    private String microservice;

    private String actionRus;

    private String username;

    private Integer requestStatus;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime date;

    private String logType;

    public LogJSONInfoData json;

    private Integer duration;
}
