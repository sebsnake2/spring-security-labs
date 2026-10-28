package com.example.lab1.quartz;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class QuartzPersistenceConfigurationTest {

    @Test
    @DisplayName("Quartz uses JDBC store and Spring schema initialization is disabled")
    void quartz_usesJdbcStoreWithoutBootSchemaInitialization() {
        YamlPropertiesFactoryBean yaml =
                new YamlPropertiesFactoryBean();

        yaml.setResources(
                new ClassPathResource(
                        "application.yaml"
                )
        );

        Properties properties =
                yaml.getObject();

        assertNotNull(properties);

        assertEquals(
                "jdbc",
                properties.getProperty(
                        "spring.quartz.job-store-type"
                )
        );

        assertEquals(
                "never",
                properties.getProperty(
                        "spring.quartz.jdbc.initialize-schema"
                )
        );

        assertEquals(
                "QRTZ_",
                properties.getProperty(
                        "spring.quartz.properties.org.quartz.jobStore.tablePrefix"
                )
        );
    }

    @Test
    @DisplayName("Quartz tables are managed by Liquibase changelog")
    void quartzTables_areManagedByLiquibase()
            throws IOException {

        String masterChangelog =
                readResource(
                        "db/changelog/db.changelog-master.yaml"
                );

        String quartzSchema =
                readResource(
                        "db/changelog/006-quartz-schema.sql"
                );

        assertTrue(
                masterChangelog.contains(
                        "006-quartz-schema.sql"
                )
        );

        assertTrue(
                quartzSchema.contains(
                        "CREATE TABLE QRTZ_JOB_DETAILS"
                )
        );

        assertTrue(
                quartzSchema.contains(
                        "CREATE TABLE QRTZ_TRIGGERS"
                )
        );

        assertTrue(
                quartzSchema.contains(
                        "CREATE TABLE QRTZ_CRON_TRIGGERS"
                )
        );

        assertTrue(
                quartzSchema.contains(
                        "CREATE TABLE QRTZ_LOCKS"
                )
        );
    }

    private String readResource(
            String path
    ) throws IOException {

        ClassPathResource resource =
                new ClassPathResource(path);

        try (var inputStream =
                     resource.getInputStream()) {

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}