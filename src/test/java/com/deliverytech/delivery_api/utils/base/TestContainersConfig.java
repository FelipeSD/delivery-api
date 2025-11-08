package com.deliverytech.delivery_api.utils.base;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

@Testcontainers
public abstract class TestContainersConfig {

  @Container
  @SuppressWarnings("resource")
  static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
      .withDatabaseName("delivery")
      .withUsername("postgres")
      .withPassword("postgres");

  @Container
  static GenericContainer<?> redis = new RedisContainer(DockerImageName.parse("redis:7.4-alpine"));

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    // Banco
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);

    // Redis
    // registry.add("spring.data.redis.host", redis::getHost);
    // registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));

    // Cache (habilita Redis)
    registry.add("spring.cache.type", () -> "none");
  }
}
