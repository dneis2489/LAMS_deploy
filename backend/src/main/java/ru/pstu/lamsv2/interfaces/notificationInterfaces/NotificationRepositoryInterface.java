package ru.pstu.lamsv2.interfaces.notificationInterfaces;

import ru.pstu.lamsv2.dto.getDataInDB.notificationDTO.NotificationSettingDTO;
import ru.pstu.lamsv2.dto.getDataInDB.notificationDTO.PendingNotificationDTO;
import ru.pstu.lamsv2.enums.NotificationCategory;

import java.util.List;
import java.util.UUID;

public interface NotificationRepositoryInterface
{
    List<NotificationSettingDTO> getSettings(UUID userId);

    void updateSettings(UUID userId, List<NotificationCategory> enabledCategories);

    void enqueue(NotificationCategory category, String subject, String body);

    List<PendingNotificationDTO> getPending(int limit);

    List<String> getRecipientEmails(String categoryCode);

    void markSent(long notificationId);

    void markFailed(long notificationId, String errorMessage);
}
