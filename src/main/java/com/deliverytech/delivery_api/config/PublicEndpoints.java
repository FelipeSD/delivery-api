package com.deliverytech.delivery_api.config;

public final class PublicEndpoints {

  private PublicEndpoints() {
  } // evita instanciar

  public static final String[] ENDPOINTS = {
      // 🔐 Autenticação
      "/api/auth/**",

      // 🍽️ Endpoints públicos da aplicação
      "/api/restaurantes/**",
      "/api/produtos/**",
      "/dashboard/**",

      // 📘 Swagger / OpenAPI
      "/v3/api-docs/**",
      "/api-docs/**",
      "/swagger-ui.html",
      "/swagger-ui/**",
      "/scalar/**",

      // 🧠 Banco e ferramentas de debug
      "/h2-console/**",
  };
}
