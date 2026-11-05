package com.example.lab1.multimodule;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MultiModuleStructureTest {

    private static Path root;

    @BeforeAll
    static void findProjectRoot() {
        Path current = Path.of("").toAbsolutePath();

        while (current != null) {
            Path pom = current.resolve("pom.xml");

            if (Files.exists(pom)) {
                try {
                    String content = Files.readString(pom);

                    if (content.contains("<module>task-service</module>")
                            && content.contains("<module>scheduler-service</module>")) {
                        root = current;
                        return;
                    }
                } catch (IOException ignored) {
                }
            }

            current = current.getParent();
        }

        fail("Multi-module project root was not found");
    }

    @Test
    @DisplayName("Root POM aggregates both services and child modules reference parent POM")
    void rootPomDefinesModulesAndParents() throws IOException {
        String rootPom = read("pom.xml");
        String taskPom = read("task-service/pom.xml");
        String schedulerPom = read("scheduler-service/pom.xml");

        assertTrue(rootPom.contains("<packaging>pom</packaging>"));
        assertTrue(rootPom.contains("<module>task-service</module>"));
        assertTrue(rootPom.contains("<module>scheduler-service</module>"));

        assertTrue(taskPom.contains("<artifactId>spring-security-labs</artifactId>"));
        assertTrue(taskPom.contains("<relativePath>../pom.xml</relativePath>"));

        assertTrue(schedulerPom.contains("<artifactId>spring-security-labs</artifactId>"));
        assertTrue(schedulerPom.contains("<relativePath>../pom.xml</relativePath>"));
    }

    @Test
    @DisplayName("Shared test dependency is declared only in root POM")
    void sharedDependencyIsDeclaredInRootPom() throws IOException {
        String rootPom = read("pom.xml");
        String taskPom = read("task-service/pom.xml");
        String schedulerPom = read("scheduler-service/pom.xml");

        assertTrue(rootPom.contains("<artifactId>spring-boot-starter-test</artifactId>"));

        assertFalse(taskPom.contains("<artifactId>spring-boot-starter-test</artifactId>"));
        assertFalse(schedulerPom.contains("<artifactId>spring-boot-starter-test</artifactId>"));
    }

    @Test
    @DisplayName("Each service has its own main class, configuration, port, plugin and Dockerfile")
    void servicesHaveIndependentRuntimeConfiguration() throws IOException {
        assertTrue(Files.exists(root.resolve(
                "task-service/src/main/java/com/example/lab1/TaskServiceApplication.java")));

        assertTrue(Files.exists(root.resolve(
                "scheduler-service/src/main/java/com/example/scheduler/SchedulerServiceApplication.java")));

        assertTrue(Files.exists(root.resolve("task-service/Dockerfile")));
        assertTrue(Files.exists(root.resolve("scheduler-service/Dockerfile")));

        String taskYaml = read("task-service/src/main/resources/application.yml");
        String schedulerYaml = read("scheduler-service/src/main/resources/application.yml");

        assertTrue(taskYaml.contains("port: 8080"));
        assertTrue(schedulerYaml.contains("port: 8081"));

        String taskPom = read("task-service/pom.xml");
        String schedulerPom = read("scheduler-service/pom.xml");

        assertTrue(taskPom.contains("<artifactId>spring-boot-maven-plugin</artifactId>"));
        assertTrue(schedulerPom.contains("<artifactId>spring-boot-maven-plugin</artifactId>"));
    }

    @Test
    @DisplayName("Services do not depend on each other")
    void servicesDoNotDependOnEachOther() throws IOException {
        String taskPom = read("task-service/pom.xml");
        String schedulerPom = read("scheduler-service/pom.xml");

        assertFalse(taskPom.contains("<artifactId>scheduler-service</artifactId>"));
        assertFalse(schedulerPom.contains("<artifactId>task-service</artifactId>"));
    }

    private static String read(String relativePath) throws IOException {
        return Files.readString(root.resolve(relativePath));
    }
}