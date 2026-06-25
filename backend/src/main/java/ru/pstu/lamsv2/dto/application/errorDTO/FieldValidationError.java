package ru.pstu.lamsv2.dto.application.errorDTO;

public record FieldValidationError(
        String field,
        String message
)
{
}
