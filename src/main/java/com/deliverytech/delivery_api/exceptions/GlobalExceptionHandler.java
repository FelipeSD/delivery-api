package com.deliverytech.delivery_api.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(error.getField(), error.getDefaultMessage());
    }
    return buildError("VALIDATION_ERROR", "Erro de validação", HttpStatus.BAD_REQUEST, fieldErrors);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
    return buildError("BUSINESS_ERROR", ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
    return buildError("ENTITY_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
    return buildError("INTERNAL_ERROR", "Erro interno inesperado", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<Map<String, Object>> buildError(String code, String message, HttpStatus status) {
    Map<String, Object> error = Map.of("code", code, "message", message);
    Map<String, Object> body = Map.of("success", false, "error", error);
    return ResponseEntity.status(status).body(body);
  }

  private ResponseEntity<Map<String, Object>> buildError(String code, String message, HttpStatus status,
      Map<String, String> details) {
    Map<String, Object> error = new HashMap<>();
    error.put("code", code);
    error.put("message", message);
    error.put("details", details);

    Map<String, Object> body = new HashMap<>();
    body.put("success", false);
    body.put("error", error);

    return ResponseEntity.status(status).body(body);
  }
}
