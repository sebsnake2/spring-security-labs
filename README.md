# Spring Security Labs

Spring Boot project for university laboratory works.

The same project is extended step by step with each new lab.

## Implemented labs

### Lab 1 — Spring Security / Keycloak / CRUD

- Spring Boot REST API
- PostgreSQL + Liquibase
- Keycloak authentication
- JWT Bearer authentication
- Role-based authorization
- `@PreAuthorize`
- Thymeleaf UI

### Lab 2 — Unit Testing

- JUnit 5
- Mockito
- Unit tests for `TaskService`
- 100% line and method coverage for `TaskService`
- Mutation testing demonstration

### Lab 3 — Custom Annotations

Implemented three custom annotation mechanisms:

- Bean Validation annotation
- Composed Spring MVC annotation
- Custom `HandlerMethodArgumentResolver`

### Lab 4 — Spring AOP

Implemented three custom AOP annotations and aspects:

- `@MeasureExecutionTime` — `@Around`
- `@CountInvocation` — `@Before`
- `@RejectNullResult` — `@AfterReturning`

Also demonstrated Spring AOP self-invocation behavior:

- external call through proxy triggers the aspect
- internal self-invocation does not trigger the aspect

#### Lab 4 Tests

- `ExecutionTimeAspectTest`
- `InvocationCountingAspectTest`
- `NullResultGuardAspectTest`

All 5 tests pass successfully.

### Lab 5 — Async Processing

Implemented asynchronous processing with a custom limited thread pool:

- Fire-and-forget operation using `@Async`
- Custom `ThreadPoolTaskExecutor` with explicit pool and queue limits
- Parallel independent operations using `CompletableFuture`
- Result combination using `thenCombine`
- Exception handling with `exceptionally`
- `AsyncUncaughtExceptionHandler` for `void @Async` methods
- Timeout handling for long-running Future operations
- Predictable executor overload handling with task rejection

#### Lab 5 Tests

`AsyncProcessingTest` covers 6 scenarios:

- Fire-and-forget returns before the slow operation finishes
- Async methods use the configured executor
- Independent async calls execute concurrently
- Async failures are handled without crashing the caller
- Future waiting is limited by timeout
- Executor overload is handled predictably

All 6 tests pass successfully.

### Lab 6 — Quartz Scheduler

Implemented persistent scheduled jobs using Quartz Scheduler and PostgreSQL:

- Quartz JDBC JobStore with PostgreSQL
- Quartz `QRTZ_*` schema managed by Liquibase
- Spring Boot schema initialization disabled with `initialize-schema: never`
- Two lightweight Quartz jobs
- Three triggers, including two triggers for one job
- Explicit `JobKey`, trigger names and groups
- `JobDataMap` defaults with trigger-level overrides
- Explicit misfire instructions
- Cron triggers with `Europe/Kyiv` timezone
- `@DisallowConcurrentExecution` for non-concurrent job execution
- Idempotent job processing using unique execution keys
- Transaction boundaries located in Spring service methods
- Explicit failure strategy without unlimited retries
- Custom Spring-aware Quartz JobFactory

#### Lab 6 Tests

Implemented 13 tests covering:

- Job and trigger identities and relationships
- `JobDataMap` trigger override behavior
- Explicit misfire policies
- Cron timezone configuration
- Lightweight Job-to-Service delegation
- Missing JobDataMap parameter handling
- `@DisallowConcurrentExecution`
- Failure handling without immediate endless retry
- Transaction boundaries on service methods
- Idempotent report execution
- Idempotent reminder execution
- JDBC JobStore configuration
- Quartz schema management through Liquibase

All 13 Lab 6 tests pass successfully.

### Lab 7 — Multi-module Maven

The project was converted into a Maven multi-module architecture with two independent Spring Boot services.

#### Modules

- `task-service` — main application with task management, security, validation, AOP and asynchronous processing
- `scheduler-service` — independent Quartz Scheduler service

#### Implemented

- root Maven POM with `packaging` set to `pom`
- `task-service` and `scheduler-service` declared in the root `<modules>` section
- both modules inherit from the root parent POM
- shared test dependency declared in the root POM
- each service has its own `pom.xml`
- each service has its own Spring Boot main class
- independent `application.yml` configuration for each service
- `task-service` runs on port `8080`
- `scheduler-service` runs on port `8081`
- Spring Boot Maven plugin configured for both modules
- services do not have Maven dependencies on each other
- separate Dockerfile for each service
- Quartz logic and configuration moved into `scheduler-service`
- root Maven reactor build successfully builds and verifies all modules

#### Lab 7 Tests

Implemented `MultiModuleStructureTest` with 4 tests covering:

- root POM aggregation and child parent configuration
- shared dependency configuration
- independent service runtime configuration
- absence of dependencies between services

All Lab 7 tests pass successfully.

The complete Maven reactor build also finishes with `BUILD SUCCESS` for:

- `spring-security-labs`
- `task-service`
- `scheduler-service`

## Technologies

- Java 21
- Spring Boot 4
- Spring Security
- Spring AOP
- Spring Async
- Keycloak
- PostgreSQL
- Liquibase
- JUnit 5
- Mockito
- Maven

### Lab 8 — Message Brokers

The project was extended with asynchronous communication between `task-service` and `scheduler-service` using RabbitMQ.

#### Architecture
- `task-service` acts as the event producer
- `scheduler-service` acts as the event consumer
- RabbitMQ is used as the message broker
- services do not communicate through HTTP for this process
- services do not depend on each other's Java classes

#### Implemented
- RabbitMQ broker configured in `compose.yml`
- durable `task.events.exchange`
- durable `task.events.scheduler` queue
- routing key `task.events`
- persistent RabbitMQ messages
- business events are published only after successful transaction commit
- failed or rolled-back transactions do not publish events
- `TASK_CREATED`, `TASK_COMPLETED` and `TASK_DELETED` events
- consumer stores processed events in PostgreSQL
- `processed_task_events` table added through Liquibase
- duplicate events are ignored using unique `event_id` and `ON CONFLICT DO NOTHING`
- events remain in RabbitMQ while the consumer is unavailable and are processed after it starts again
- RabbitMQ host is configured through `RABBITMQ_HOST`
- local IDE execution uses `localhost`
- Docker execution uses the Compose service name `rabbitmq`
- each service remains independently runnable

#### Lab 8 Tests
12 Lab 8 tests were added:

- `TaskMessagingTest` — 4 tests
- `RabbitMessagingConfigurationTest` — 4 tests
- `TaskEventProcessingTest` — 4 tests

The complete Maven reactor also successfully passes all previous and current tests:

- `task-service` — 36 tests passed
- `scheduler-service` — 17 tests passed
- 53 tests passed in total
- `mvn clean verify` — `BUILD SUCCESS`