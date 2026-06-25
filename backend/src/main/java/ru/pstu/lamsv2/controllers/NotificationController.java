package ru.pstu.lamsv2.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pstu.lamsv2.dto.application.notificationDTO.UpdateNotificationSettingsDTO;
import ru.pstu.lamsv2.dto.getDataInDB.notificationDTO.NotificationCategoryDTO;
import ru.pstu.lamsv2.dto.getDataInDB.notificationDTO.NotificationSettingDTO;
import ru.pstu.lamsv2.interfaces.notificationInterfaces.NotificationServiceInterface;
import ru.pstu.lamsv2.security.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("lams/notifications")
public class NotificationController
{
    private final NotificationServiceInterface notificationService;

    public NotificationController(NotificationServiceInterface notificationService)
    {
        this.notificationService = notificationService;
    }

    @GetMapping("/categories")
    public List<NotificationCategoryDTO> getCategories()
    {
        return notificationService.getCategories();
    }

    @GetMapping("/settings")
    public List<NotificationSettingDTO> getSettings(@AuthenticationPrincipal CustomUserDetails user)
    {
        return notificationService.getSettings(user.getId());
    }

    @PutMapping("/settings")
    public ResponseEntity<Void> updateSettings(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody UpdateNotificationSettingsDTO request
    )
    {
        notificationService.updateSettings(user.getId(), request.getEnabledCategories());
        return ResponseEntity.noContent().build();
    }
}
