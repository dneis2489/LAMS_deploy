package ru.pstu.lamsv2.dto.application.logListFilterDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JsonTextFilterDTO
{
    private String key;

    private String value;

    private String operator = "contains";

    public JsonTextFilterDTO(String key, String value)
    {
        this.key = key;
        this.value = value;
    }
}
