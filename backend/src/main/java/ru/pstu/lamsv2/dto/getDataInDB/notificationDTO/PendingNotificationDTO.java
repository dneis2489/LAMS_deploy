package ru.pstu.lamsv2.dto.getDataInDB.notificationDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PendingNotificationDTO
{
    private long id;
    private String categoryCode;
    private String subject;
    private String body;
}
