package com.deliverytech.delivery_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class ScalarConfig implements WebMvcConfigurer {
  @Override
  public void addViewControllers(@NonNull ViewControllerRegistry registry) {

    // Redirecionar /api-docs para interface Scalar
    registry.addRedirectViewController("/api-docs/scalar", "/scalar/index.html");
  }
}