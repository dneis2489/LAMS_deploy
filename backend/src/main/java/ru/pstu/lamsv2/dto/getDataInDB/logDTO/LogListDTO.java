package ru.pstu.lamsv2.dto.getDataInDB.logDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 DTO для получения перечня логов. Содержит краткую информацию по логам.
*/

@Getter
@Setter
@AllArgsConstructor
public class LogListDTO
{

    private long id;

    private String microservice;

    private String actionRus;

    private String username;

    private Integer requestStatus;

    private Integer startStatus;

    private Integer finishStatus;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime date;

    private String logType;
}
