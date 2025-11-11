package com.deliverytech.delivery_api.utils.base;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {
  private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:7.4-alpine");

  @Bean
  @SuppressWarnings("resource")
  @ServiceConnection("redis")
  public RedisContainer redisContainer() {
    return new RedisContainer(REDIS_IMAGE).withExposedPorts(6379);
  }
}
