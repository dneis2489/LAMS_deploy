package ru.pstu.lamsv2.dto.application.notificationDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateNotificationSettingsDTO
{
    @NotNull
    private List<String> enabledCategories;
}
