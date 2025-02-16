package com.kirilloff.taskmanager.integration;

import static org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(POSTGRES_IMAGE)
      .withDatabaseName(POSTGRES_DB_NAME)
      .withUsername(POSTGRES_USERNAME)
      .withPassword(POSTGRES_PASSWORD);


  @DynamicPropertySource
  static void initProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url",
        () -> "jdbc:postgresql://" + postgreSQLContainer.getHost() + ":"
            + postgreSQLContainer.getMappedPort(POSTGRESQL_PORT) + "/"
            + postgreSQLContainer.getDatabaseName());
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
  }
}
