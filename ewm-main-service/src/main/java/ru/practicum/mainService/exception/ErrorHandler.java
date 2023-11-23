package ru.practicum.mainService.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.toString(), "Provided method argument is not valid", e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError onValidationException(ValidationException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.toString(), "Validation error.", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(InputValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError onInputValidationException(InputValidationException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.toString(), "Incorrect input", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable e) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Server error.", e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(ForbiddenOperation.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError onForbiddenOperationException(ForbiddenOperation e) {
        return new ApiError(HttpStatus.FORBIDDEN.toString(), "Forbidden operation", e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException e) {
        return new ApiError(HttpStatus.NOT_FOUND.toString(), "Required resource is not found", e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(DataConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataConflict(DataConflictException e) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.toString(), "JsonMapping Exception", e.getMessage(),
                LocalDateTime.now());
        log.info("apiError={}", apiError);
        return apiError;
    }

    @ExceptionHandler(JsonMappingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError onJsonMappingException(JsonMappingException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.toString(), "JsonMapping Exception", e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError onMissingParamException(MissingServletRequestParameterException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.toString(), "Required parameter is missing", e.getMessage(),
                LocalDateTime.now());
    }

}



