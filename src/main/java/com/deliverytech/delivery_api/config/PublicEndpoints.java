package com.deliverytech.delivery_api.config;

public final class PublicEndpoints {

  private PublicEndpoints() {
  } // evita instanciar

  public static final String[] ENDPOINTS = {
      "/api/auth/**",
      "/api/restaurantes/**",
      "/api/produtos/**",
      "/v3/api-docs/**",
      "/api-docs/**",
      "/swagger-ui.html",
      "/swagger-ui/**",
      "/scalar/**",
      "/h2-console/**",
      "/health"
  };
}