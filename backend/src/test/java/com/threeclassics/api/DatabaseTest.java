package com.threeclassics.api;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Integration tests run against PostgreSQL: a Testcontainers instance when Docker is available,
 * or the database named by TEST_DATABASE_URL (plus TEST_DATABASE_USER/PASSWORD) when it is not.
 */
abstract class DatabaseTest {

    private static final String URL = System.getenv("TEST_DATABASE_URL");
    private static PostgreSQLContainer<?> container;

    /** Used by {@code @EnabledIf} so the suite is skipped, not failed, without any database. */
    static boolean databaseAvailable() {
        return URL != null || DockerClientFactory.instance().isDockerAvailable();
    }

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        if (URL != null) {
            registry.add("spring.datasource.url", () -> URL);
            registry.add("spring.datasource.username", () -> env("TEST_DATABASE_USER", "threeclassics"));
            registry.add("spring.datasource.password", () -> env("TEST_DATABASE_PASSWORD", "threeclassics"));
            return;
        }
        synchronized (DatabaseTest.class) {
            if (container == null) {
                container = new PostgreSQLContainer<>("postgres:16-alpine");
                container.start();
            }
        }
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null ? fallback : value;
    }
}
