## Purpose

Short, focused guidance for AI coding agents working on this Spring Boot service (Tasks API).
The goal: be immediately productive — understand architecture, conventions, build/test workflow, and integration points.

## Big picture (what this project is)

- Java Spring Boot service exposing REST endpoints under `/api/tasks`.
- Layered structure: `controller` -> `service` -> `repository` (Spring Data JPA) with `model` entities and simple DTOs in `dto`.
- Single-module Maven project: see `pom.xml` for dependencies (Spring Boot, Data JPA, Spring Cloud Stream, OpenFeign).
- Main entry: `TasksApiApplication` (component scanning configured for `com.eXiua.tasksi`).

Key files to inspect for examples and patterns:
- `src/main/java/com/eXiua/tasksi/controller/TaskController.java` — REST patterns, pagination, request headers (`X-Actor-Id`).
- `src/main/java/com/eXiua/tasksi/service/TaskServiceImpl.java` — business logic, DTO conversion (BeanUtils), KPI calculations.
- `src/main/java/com/eXiua/tasksi/repository/TaskRepository.java` — JpaRepository + JpaSpecificationExecutor usage for dynamic queries.
- `src/main/java/com/eXiua/tasksi/model/Task.java` — entity fields, lifecycle hooks (`@PrePersist`, `@PreUpdate`) and enum usage for status/priority.
- `src/main/resources/application.properties` — default runtime uses H2 in-memory DB and exposes `/h2-console`.

## Important conventions and project-specific patterns

- Controllers accept an optional header `X-Actor-Id` and pass it to service methods for audit/history. Follow this when adding endpoints.
- DTOs are plain Java classes under `dto/` (mutable public fields + getters/setters). Use existing `TaskDTO` as canonical shape when returning API responses.
- Service implementations convert between entity and DTO using `BeanUtils.copyProperties(...)`. When adding fields, keep conversion consistent (see `toDTO` / `fromDTO`).
- Search/filter uses JPA `Specification` (see `TaskServiceImpl.search(...)`). Prefer adding filter predicates there to keep controllers thin.
- KPI logic is implemented in `TaskServiceImpl.kpis()` — it's a reference for aggregate queries and in-memory computations.

## Integration points and external systems

- Database: default is H2 in-memory (see `application.properties`). Production-ready configuration expects Postgres (dependency present in `pom.xml`).
- Messaging: `spring-cloud-stream` and RabbitMQ config hints are in `application.properties` (rabbitmq host/port). If adding stream bindings, follow spring-cloud-stream conventions and register binders in `application.properties`.
- External APIs: `spring-cloud-openfeign` is included; follow the project's Feign client conventions if you add cross-service calls.

## Build / run / test (developer workflow)

- On Windows (PowerShell) use the provided wrapper at project root:
  - Run tests: `.\\mvnw.cmd test`
  - Run the app: `.\\mvnw.cmd spring-boot:run` or build and run the jar: `.\\mvnw.cmd package` then `java -jar target/tasks_api-0.0.1-SNAPSHOT.jar`
- H2 console: available at `http://localhost:8080/h2-console` when running locally (see `application.properties`). JDBC URL: `jdbc:h2:mem:testdb`.
- Test reports are generated under `target/surefire-reports`.

## When editing code, follow these concrete examples

- Adding a new REST endpoint: copy the pattern in `TaskController` (use `@RestController`, map under `/api/tasks`, reuse pagination and header handling). Keep controllers small — delegate logic to `TaskService`.
- Adding filters/search: modify `TaskServiceImpl.search(...)` using JPA Specifications and `taskRepository.findAll(spec, pageable)`.
- Persisting history/audit: existing pattern uses `TaskHistory` repository inside `TaskServiceImpl` to record actions (CREATED/UPDATED/DELETED) with the `actorId` — follow that approach.

## Quick examples (where to change code)

- Add endpoint: `src/main/java/com/eXiua/tasksi/controller/TaskController.java`
- Add business logic: `src/main/java/com/eXiua/tasksi/service/TaskServiceImpl.java`
- Add DB mapping: `src/main/java/com/eXiua/tasksi/model/` (entity + enums)

## What to avoid / project-specific gotchas

- Do not assume a separate security layer — actor identity is passed via `X-Actor-Id` header; do not remove it when modifying endpoints.
- DTOs and entities are manually mapped with `BeanUtils`; adding new fields requires updating both `fromDTO`/`toDTO` and DTO class.
- The code uses `JpaSpecificationExecutor` for dynamic queries — avoid ad-hoc SQL unless necessary.

## Where to look for tests and examples

- Unit tests: `src/test/java/...` and Maven surefire output in `target/surefire-reports`.
- Example flows: creating/updating tasks in `TaskServiceImpl` demonstrates lifecycle and history recording.

## If you need clarification

- Ask where to put a new REST path or whether a change should be handled in service vs repository.
- If integrating external services, confirm whether to use Feign (already available) or a direct HTTP client.

---
If you want, I can: (1) merge this into an existing file if you already have an older `.github/copilot-instructions.md`, or (2) expand sections into more explicit code snippets and commands. Which would you prefer?
