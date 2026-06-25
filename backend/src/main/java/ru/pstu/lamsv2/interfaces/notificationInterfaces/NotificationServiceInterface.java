package ru.pstu.lamsv2.interfaces.notificationInterfaces;

import ru.pstu.lamsv2.dto.getDataInDB.notificationDTO.NotificationCategoryDTO;
import ru.pstu.lamsv2.dto.getDataInDB.notificationDTO.NotificationSettingDTO;
import ru.pstu.lamsv2.enums.NotificationCategory;

import java.util.List;
import java.util.UUID;

public interface NotificationServiceInterface
{
    List<NotificationCategoryDTO> getCategories();

    List<NotificationSettingDTO> getSettings(UUID userId);

    void updateSettings(UUID userId, List<String> enabledCategories);

    void enqueue(NotificationCategory category, String subject, String body);

    void sendPending();
}
