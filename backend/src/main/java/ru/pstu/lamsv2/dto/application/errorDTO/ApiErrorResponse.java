package ru.pstu.lamsv2.dto.application.errorDTO;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        String requestId,
        List<FieldValidationError> validationErrors
)
{
}
