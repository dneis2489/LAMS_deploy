package ru.pstu.lamsv2.cron;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pstu.lamsv2.interfaces.notificationInterfaces.NotificationServiceInterface;

@Component
public class RunNotificationOutboxWithCron
{
    private final NotificationServiceInterface notificationService;

    public RunNotificationOutboxWithCron(NotificationServiceInterface notificationService)
    {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void sendPendingNotifications()
    {
        notificationService.sendPending();
    }
}
