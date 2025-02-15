package com.kirilloff.taskmanager.integration;

import static org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseIntegrationTest {

  private static final String POSTGRES_IMAGE = "postgres:16.4";
  private static final String POSTGRES_DB_NAME = "test_db";
  private static final String POSTGRES_USERNAME = "postgres";
  private static final String POSTGRES_PASSWORD = "postgres";

  private static final String REDIS_IMAGE = "redis:latest";
  private static final int REDIS_PORT = 6379;

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(POSTGRES_IMAGE)
      .withDatabaseName(POSTGRES_DB_NAME)
      .withUsername(POSTGRES_USERNAME)
      .withPassword(POSTGRES_PASSWORD);

  @Container
  @ServiceConnection
  static GenericContainer<?> redisContainer = new GenericContainer<>(REDIS_IMAGE)
      .withExposedPorts(REDIS_PORT);

  @DynamicPropertySource
  static void initProps(DynamicPropertyRegistry registry) {
    // Настройка для Postgresql
    registry.add("spring.datasource.url",
        () -> "jdbc:postgresql://" + postgreSQLContainer.getHost() + ":"
            + postgreSQLContainer.getMappedPort(POSTGRESQL_PORT) + "/"
            + postgreSQLContainer.getDatabaseName());
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

    // Настройка для Redis
    registry.add("spring.data.redis.host", redisContainer::getHost);
    registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(REDIS_PORT));
  }
}
