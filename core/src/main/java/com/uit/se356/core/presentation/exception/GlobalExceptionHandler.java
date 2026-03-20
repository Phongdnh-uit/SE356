package com.uit.se356.core.presentation.exception;

import com.uit.se356.common.dto.ErrorResponse;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

  private final MessageSource messageSource;

  @ApiResponse(
      responseCode = "400",
      description = "Bad Request",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)))
  @ExceptionHandler(AppException.class)
  public ResponseEntity<ErrorResponse> handleAppException(
      AppException ex, HttpServletRequest request) {
    String message =
        messageSource.getMessage(
            ex.getErrorCode().getMessageKey(), null, LocaleContextHolder.getLocale());
    // Chuyển detail trong exception nếu là dạng map
    Map<String, String> errors = new HashMap<>();
    // Chuyển detail nếu là list field error
    if (ex.getDetails() instanceof List list
        && !list.isEmpty()
        && list.get(0) instanceof FieldError) {
      @SuppressWarnings("unchecked")
      List<FieldError> fieldErrors = (List<FieldError>) ex.getDetails();
      fieldErrors.forEach(
          fieldError -> {
            String localizedMessage =
                messageSource.getMessage(
                    fieldError.messageKey(), fieldError.args(), LocaleContextHolder.getLocale());
            errors.put(fieldError.field(), localizedMessage);
          });
    }

    ErrorResponse errorResponse =
        new ErrorResponse(
            request.getRequestURI(),
            ex.getErrorCode().getHttpStatus(),
            message.isBlank() ? ex.getMessage() : message,
            errors,
            ex.getErrorCode().getCode());

    // Chỉ ghi log với các lỗi nghiêm trọng của server, còn các lỗi client (4xx) thì không cần log
    // hoặc log ở mức độ thấp hơn
    if (ex.getErrorCode().getHttpStatus() >= 500) {
      log.error("CRITICAL ERROR: {}", errorResponse, ex);
    } else {
      log.warn("Bussiness Warning: {}", errorResponse);
    }
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String message =
        messageSource.getMessage(
            CommonErrorCode.VALIDATION_ERROR.getMessageKey(),
            null,
            LocaleContextHolder.getLocale());
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            fieldError -> {
              fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
    ErrorResponse errorResponse =
        new ErrorResponse(
            request.getRequestURI(),
            CommonErrorCode.VALIDATION_ERROR.getHttpStatus(),
            message,
            fieldErrors,
            CommonErrorCode.VALIDATION_ERROR.getCode());
    log.warn("Bussiness Warning: {}", errorResponse);
    return ResponseEntity.status(CommonErrorCode.VALIDATION_ERROR.getHttpStatus())
        .body(errorResponse);
  }

  @ExceptionHandler({Exception.class, RuntimeException.class})
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex, HttpServletRequest request) {
    String message =
        messageSource.getMessage(
            CommonErrorCode.INTERNAL_ERROR.getMessageKey(), null, LocaleContextHolder.getLocale());
    ErrorResponse errorResponse =
        new ErrorResponse(
            request.getRequestURI(),
            CommonErrorCode.INTERNAL_ERROR.getHttpStatus(),
            message,
            null,
            CommonErrorCode.INTERNAL_ERROR.getCode());
    log.error("CRITICAL ERROR: {}", errorResponse, ex);
    return ResponseEntity.status(CommonErrorCode.INTERNAL_ERROR.getHttpStatus())
        .body(errorResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, HttpServletRequest request) {

    // Thử trích xuất AppException từ "nhân" của lỗi Jackson
    Throwable cause = ex.getCause();
    if (cause instanceof tools.jackson.databind.exc.ValueInstantiationException vie
        && vie.getCause() instanceof AppException appEx) {
      // Nếu đúng là do validation trong Record/Command, dùng lại hàm xử lý AppException
      return handleAppException(appEx, request);
    }

    // Nếu là lỗi format JSON bình thường (ví dụ thiếu dấu phẩy, sai ngoặc)
    String message = "Invalid JSON format";
    ErrorResponse errorResponse =
        new ErrorResponse(
            request.getRequestURI(),
            HttpStatus.BAD_REQUEST.value(),
            message,
            null,
            CommonErrorCode.VALIDATION_ERROR.getCode());

    log.warn("Bussiness Warning: {}", errorResponse, ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }
}
