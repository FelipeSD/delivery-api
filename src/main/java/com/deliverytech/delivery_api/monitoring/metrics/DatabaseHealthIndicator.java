package com.deliverytech.delivery_api.monitoring.metrics;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("database")
public class DatabaseHealthIndicator implements HealthIndicator {

  private final DataSource dataSource;

  public DatabaseHealthIndicator(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Health health() {
    try (Connection connection =

        dataSource.getConnection()) {
      if (connection.isValid(1)) {
        return Health.up()
            .withDetail("database", "H2")
            .withDetail("status", "Conectado")
            .withDetail("valida onQuery", "SELECT 1")
            .build();
      }
    } catch (SQLException e) {
      return Health.down()
          .withDetail("database", "H2")
          .withDetail("error", e.getMessage())
          .build();
    }
    return Health.down()
        .withDetail("database", "H2")
        .withDetail("error", "Conexão inválida")
        .build();
  }
}
