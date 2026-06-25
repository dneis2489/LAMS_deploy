package ru.pstu.lamsv2.services.notificationServices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.pstu.lamsv2.dto.getDataInDB.notificationDTO.PendingNotificationDTO;
import ru.pstu.lamsv2.enums.NotificationCategory;
import ru.pstu.lamsv2.interfaces.notificationInterfaces.NotificationRepositoryInterface;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest
{
    @Mock
    private NotificationRepositoryInterface notificationRepository;

    @Mock
    private ObjectProvider<JavaMailSender> mailSenderProvider;

    @Mock
    private JavaMailSender mailSender;

    private NotificationService notificationService;

    @BeforeEach
    void setUp()
    {
        notificationService = new NotificationService(
                notificationRepository,
                mailSenderProvider,
                true,
                "no-reply@lams.local"
        );
    }

    @Test
    void updateSettingsConvertsMultipleCategoryCodes()
    {
        UUID userId = UUID.randomUUID();

        notificationService.updateSettings(
                userId,
                List.of("ANOMALY_DETECTED", "ERROR_LOG_RECEIVED")
        );

        ArgumentCaptor<List<NotificationCategory>> categoriesCaptor = ArgumentCaptor.forClass(List.class);
        verify(notificationRepository).updateSettings(eq(userId), categoriesCaptor.capture());

        assertThat(categoriesCaptor.getValue()).containsExactly(
                NotificationCategory.ANOMALY_DETECTED,
                NotificationCategory.ERROR_LOG_RECEIVED
        );
    }

    @Test
    void sendPendingSendsMessageToEveryEnabledRecipientAndMarksNotificationSent()
    {
        PendingNotificationDTO notification = new PendingNotificationDTO(
                42L,
                NotificationCategory.ERROR_FORECAST.name(),
                "Forecast warning",
                "There are too many predicted errors."
        );

        when(mailSenderProvider.getIfAvailable()).thenReturn(mailSender);
        when(notificationRepository.getPending(50)).thenReturn(List.of(notification));
        when(notificationRepository.getRecipientEmails(NotificationCategory.ERROR_FORECAST.name()))
                .thenReturn(List.of("admin@lams.local", "ops@lams.local"));

        notificationService.sendPending();

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(2)).send(messageCaptor.capture());
        verify(notificationRepository).markSent(42L);
        verify(notificationRepository, never()).markFailed(anyLong(), anyString());

        assertThat(messageCaptor.getAllValues())
                .extracting(message -> message.getTo()[0])
                .containsExactly("admin@lams.local", "ops@lams.local");
        assertThat(messageCaptor.getAllValues())
                .allSatisfy(message -> {
                    assertThat(message.getFrom()).isEqualTo("no-reply@lams.local");
                    assertThat(message.getSubject()).isEqualTo("Forecast warning");
                    assertThat(message.getText()).isEqualTo("There are too many predicted errors.");
                });
    }

    @Test
    void sendPendingDoesNothingWhenEmailDeliveryDisabled()
    {
        NotificationService disabledService = new NotificationService(
                notificationRepository,
                mailSenderProvider,
                false,
                "no-reply@lams.local"
        );

        disabledService.sendPending();

        verify(mailSenderProvider, never()).getIfAvailable();
        verify(notificationRepository, never()).getPending(50);
    }
}
