package com.deliverytech.delivery_api.common.config;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;

/**
 * Configuração central do Micrometer e integração com Actuator/Prometheus.
 * 
 * - Adiciona tags comuns a todas as métricas (application, environment,
 * version)
 * - Oculta endpoints internos do Actuator nas métricas HTTP
 * - Compatível com Prometheus, Grafana e OpenTelemetry
 */
@Configuration
public class MicrometerConfig {

  /**
   * Personaliza o registro de métricas para adicionar tags e filtros padrão.
   *
   * @return Bean configurador do Micrometer
   */
  @Bean
  MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry -> registry.config()
        // 🔖 Tags comuns aplicadas a todas as métricas
        .commonTags(
            "application", "delivery-api",
            "environment", System.getenv().getOrDefault("SPRING_PROFILES_ACTIVE", "development"),
            "version", "1.0.0")
        // 🚫 Filtro: remove métricas relacionadas ao próprio Actuator
        .meterFilter(MeterFilter.deny(id -> {
          String uri = id.getTag("uri");
          return uri != null && uri.startsWith("/actuator");
        }));
  }
}
