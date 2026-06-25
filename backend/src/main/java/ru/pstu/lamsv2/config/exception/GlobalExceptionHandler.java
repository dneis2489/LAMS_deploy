package ru.pstu.lamsv2.config.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import ru.pstu.lamsv2.dto.application.errorDTO.ApiErrorResponse;
import ru.pstu.lamsv2.dto.application.errorDTO.FieldValidationError;
import ru.pstu.lamsv2.exception.ApiException;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final List<FieldValidationError> NO_VALIDATION_ERRORS = List.of();

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
            ApiException exception,
            HttpServletRequest request
    )
    {
        return buildResponse(
                exception.getStatus(),
                exception.getCode(),
                exception.getMessage(),
                request,
                NO_VALIDATION_ERRORS
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    )
    {
        List<FieldValidationError> validationErrors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldValidationError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Некорректные данные запроса",
                request,
                validationErrors
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request
    )
    {
        List<FieldValidationError> validationErrors = exception
                .getConstraintViolations()
                .stream()
                .map(violation -> new FieldValidationError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Некорректные параметры запроса",
                request,
                validationErrors
        );
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            Exception exception,
            HttpServletRequest request
    )
    {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                "Некорректный запрос",
                request,
                NO_VALIDATION_ERRORS
        );
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            RuntimeException exception,
            HttpServletRequest request
    )
    {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                "Неверный email или пароль",
                request,
                NO_VALIDATION_ERRORS
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            AuthenticationException exception,
            HttpServletRequest request
    )
    {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                "Требуется авторизация",
                request,
                NO_VALIDATION_ERRORS
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
            AccessDeniedException exception,
            HttpServletRequest request
    )
    {
        return buildResponse(
                HttpStatus.FORBIDDEN,
                "FORBIDDEN",
                "Недостаточно прав для выполнения операции",
                request,
                NO_VALIDATION_ERRORS
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException exception,
            HttpServletRequest request
    )
    {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                "Ресурс не найден",
                request,
                NO_VALIDATION_ERRORS
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    )
    {
        return buildResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "METHOD_NOT_ALLOWED",
                "Метод запроса не поддерживается",
                request,
                NO_VALIDATION_ERRORS
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request
    )
    {
        return buildResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "UNSUPPORTED_MEDIA_TYPE",
                "Тип содержимого запроса не поддерживается",
                request,
                NO_VALIDATION_ERRORS
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    )
    {
        LOG.warn("Data integrity violation. requestId={}", getRequestId(request), exception);
        return buildResponse(
                HttpStatus.CONFLICT,
                "DATA_CONFLICT",
                "Операция нарушает ограничения данных",
                request,
                NO_VALIDATION_ERRORS
        );
    }

    @ExceptionHandler({
            DataAccessException.class,
            SQLException.class
    })
    public ResponseEntity<ApiErrorResponse> handleDatabaseException(
            Exception exception,
            HttpServletRequest request
    )
    {
        LOG.error("Database error. requestId={}", getRequestId(request), exception);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "DATABASE_ERROR",
                "Ошибка работы с базой данных",
                request,
                NO_VALIDATION_ERRORS
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request
    )
    {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                exception.getMessage(),
                request,
                NO_VALIDATION_ERRORS
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(
            Exception exception,
            HttpServletRequest request
    )
    {
        LOG.error("Unexpected error. requestId={}", getRequestId(request), exception);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Внутренняя ошибка сервера",
                request,
                NO_VALIDATION_ERRORS
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            List<FieldValidationError> validationErrors
    )
    {
        ApiErrorResponse response = new ApiErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                request.getRequestURI(),
                getRequestId(request),
                validationErrors
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    private String getRequestId(HttpServletRequest request)
    {
        String requestId = request.getHeader("X-Request-Id");
        if (requestId == null || requestId.isBlank())
        {
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }
}
