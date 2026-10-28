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