package ru.pstu.lamsv2.dto.getDataInDB.notificationDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotificationSettingDTO
{
    private String categoryCode;
    private String categoryTitle;
    private boolean enabled;
}
