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
