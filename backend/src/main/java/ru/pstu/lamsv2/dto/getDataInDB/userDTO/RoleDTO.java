package ru.pstu.lamsv2.dto.getDataInDB.userDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 DTO для получения пользовательских ролей
*/

@Getter
@Setter
@AllArgsConstructor
public class RoleDTO
{
    private Long id;

    private String name;
}
