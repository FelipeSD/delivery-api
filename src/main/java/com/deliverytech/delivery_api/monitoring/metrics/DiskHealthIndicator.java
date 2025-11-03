package com.deliverytech.delivery_api.monitoring.metrics;

import java.io.File;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DiskHealthIndicator implements HealthIndicator {
  @Override
  public Health health() {
    File root = new File(".");
    long free = root.getFreeSpace() / (1024 * 1024);
    return free > 100 ? Health.up().withDetail("freeMB", free).build()
                      : Health.down().withDetail("freeMB", free).build();
  }
}
