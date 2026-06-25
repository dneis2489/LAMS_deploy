package ru.pstu.lamsv2.services.notificationServices;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.getDataInDB.notificationDTO.NotificationCategoryDTO;
import ru.pstu.lamsv2.dto.getDataInDB.notificationDTO.NotificationSettingDTO;
import ru.pstu.lamsv2.dto.getDataInDB.notificationDTO.PendingNotificationDTO;
import ru.pstu.lamsv2.enums.NotificationCategory;
import ru.pstu.lamsv2.interfaces.notificationInterfaces.NotificationRepositoryInterface;
import ru.pstu.lamsv2.interfaces.notificationInterfaces.NotificationServiceInterface;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService implements NotificationServiceInterface
{
    private final NotificationRepositoryInterface notificationRepository;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean emailEnabled;
    private final String fromEmail;

    public NotificationService(
            NotificationRepositoryInterface notificationRepository,
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${lams.notifications.email.enabled:false}") boolean emailEnabled,
            @Value("${lams.notifications.email.from:no-reply@lams.local}") String fromEmail
    )
    {
        this.notificationRepository = notificationRepository;
        this.mailSenderProvider = mailSenderProvider;
        this.emailEnabled = emailEnabled;
        this.fromEmail = fromEmail;
    }

    @Override
    public List<NotificationCategoryDTO> getCategories()
    {
        return Arrays.stream(NotificationCategory.values())
                .map(category -> new NotificationCategoryDTO(category.name(), category.getTitle()))
                .toList();
    }

    @Override
    public List<NotificationSettingDTO> getSettings(UUID userId)
    {
        return notificationRepository.getSettings(userId);
    }

    @Override
    public void updateSettings(UUID userId, List<String> enabledCategories)
    {
        List<NotificationCategory> categories = enabledCategories.stream()
                .map(NotificationCategory::valueOf)
                .toList();
        notificationRepository.updateSettings(userId, categories);
    }

    @Override
    public void enqueue(NotificationCategory category, String subject, String body)
    {
        notificationRepository.enqueue(category, subject, body);
    }

    @Override
    public void sendPending()
    {
        if (!emailEnabled)
        {
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null)
        {
            return;
        }

        List<PendingNotificationDTO> notifications = notificationRepository.getPending(50);
        for (PendingNotificationDTO notification : notifications)
        {
            try
            {
                List<String> recipients = notificationRepository.getRecipientEmails(notification.getCategoryCode());
                if (recipients.isEmpty())
                {
                    notificationRepository.markSent(notification.getId());
                    continue;
                }

                for (String recipient : recipients)
                {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom(fromEmail);
                    message.setTo(recipient);
                    message.setSubject(notification.getSubject());
                    message.setText(notification.getBody());
                    mailSender.send(message);
                }
                notificationRepository.markSent(notification.getId());
            }
            catch (Exception ex)
            {
                notificationRepository.markFailed(notification.getId(), ex.getMessage());
            }
        }
    }
}
