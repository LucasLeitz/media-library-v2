package com.lucasleitz.medialibrary.support;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>(
            DockerImageName.parse("mysql:8.4.3"))
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("medialib");

}