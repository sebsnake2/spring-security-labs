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

## Tests

Lab 4 contains 5 passing tests:

- `ExecutionTimeAspectTest`
- `InvocationCountingAspectTest`
- `NullResultGuardAspectTest`

All tests pass successfully.

## Technologies

- Java 21
- Spring Boot 4
- Spring Security
- Spring AOP
- Keycloak
- PostgreSQL
- Liquibase
- JUnit 5
- Mockito
- Maven

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

### Lab 5 Tests

`AsyncProcessingTest` covers 6 scenarios:

- Fire-and-forget returns before the slow operation finishes
- Async methods use the configured executor
- Independent async calls execute concurrently
- Async failures are handled without crashing the caller
- Future waiting is limited by timeout
- Executor overload is handled predictably

All 6 tests pass successfully.