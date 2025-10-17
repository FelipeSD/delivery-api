package com.deliverytech.delivery_api.dtos;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Wrapper padrão para respostas da API")
public class ApiResponseWrapper<T> {

  @Schema(description = "Indica se a operação foi bem-sucedida", example = "true")
  private boolean success;

  @Schema(description = "Dados da resposta")
  private T data;

  @Schema(description = "Mensagem descri va", example = "Operação realizadaticom  sucesso")
  private String message;

  @Schema(description = "Timestamp da resposta", example = "2024-01-15T10:30:00")
  private LocalDateTime mestamp;

  public ApiResponseWrapper() {
    this.mestamp = LocalDateTime.now();
  }

  public ApiResponseWrapper(boolean success, T data, String message) {
    this.success = success;
    this.data = data;
    this.message = message;
    this.mestamp = LocalDateTime.now();
  }

  public static <T> ApiResponseWrapper<T> success(T data, String message) {
    return new ApiResponseWrapper<>(true, data, message);
  }

  public static <T> ApiResponseWrapper<T> error(String message) {
    return new ApiResponseWrapper<>(false, null, message);
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public LocalDateTime getTimestamp() {
    return mestamp;
  }

  public void setTimestamp(LocalDateTime mestamp) {
    this.mestamp = mestamp;
  }
}
