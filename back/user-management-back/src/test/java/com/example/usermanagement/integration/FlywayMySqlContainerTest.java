package com.example.usermanagement.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class FlywayMySqlContainerTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("user_management_db")
            .withUsername("app_user")
            .withPassword("app_password");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("app.security.username", () -> "admin");
        registry.add("app.security.password", () -> "password");
    }

    @Autowired
    private DataSource dataSource;

    @Test
    void contextLoadsAndFlywayRuns() {
        assertThat(mysql.isRunning()).isTrue();
    }

    @Test
    void flywayMigrationsApplied() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            // Check users table exists
            ResultSet rs = conn.getMetaData().getTables(null, null, "users", null);
            assertThat(rs.next()).isTrue();

            // Check flyway_schema_history table exists and has at least one row
            rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM flyway_schema_history");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isGreaterThan(0);
        }
    }
}
