# AGENTS.md

`crediya-autenticacion-ms` is a Java 25 / Spring Boot 4.1.1 service generated from the Bancolombia Clean Architecture Gradle plugin. Domain and API naming/messages are Spanish.

## Commands

Use `.\gradlew.bat` on Windows or `./gradlew` elsewhere; the examples use the Windows form. The build requires a Java 25 toolchain (`gradle.properties`).

- Full gate: `.\gradlew.bat build` — structure validation, compile, tests, PIT mutation tests, JaCoCo, and jars. Slow.
- Focused test:
  `.\gradlew.bat :usecase:test --tests "co.com.crediya.usecase.registrarusuario.RegistrarUsuarioUseCaseTest" -x :usecase:pitest -x :usecase:jacocoTestReport`
  Swap the module/class for other tests. Plain `test`, `check`, and `build` run PIT + JaCoCo in every module.
- Run: `.\gradlew.bat :app-service:bootRun` — port 8080; H2 console at `/h2`.
- Boot jar: `.\gradlew.bat :app-service:bootJar` -> `applications/app-service/build/libs/crediya-autenticacion-ms.jar`.
- After changing module dependencies: `.\gradlew.bat validateStructure` (also runs automatically before test/bootRun).
- No lint/format task. Coverage and mutation reports: `<module>/build/reports/jacocoHtml/index.html`, `<module>/build/reports/pitest/index.html`.

## Module map

Gradle project names differ from directory paths:

| Directory | Project | Role |
| --- | --- | --- |
| `domain/model` | `:model` | Models, gateways, exceptions; framework-free |
| `domain/usecase` | `:usecase` | Use cases; depends only on `:model` |
| `infrastructure/entry-points/api-rest` | `:api-rest` | REST controller, DTOs, exception mapping, CORS/security headers |
| `infrastructure/driven-adapters/jpa-repository` | `:jpa-repository` | JPA adapter/entities, H2 config |
| `applications/app-service` | `:app-service` | `MainApplication`, Spring wiring, `application.yaml` |

`settings.gradle` has no `include` lines; the scaffold plugin discovers these projects from the directory layout. Keep dependencies inward: `:model` is innermost, `:usecase` depends on it, adapters/entry points depend on the inner modules, and `:app-service` depends on everything. `validateStructure` enforces this.

Request flow: `ApiRest` -> `RegistrarUsuarioUseCase` -> `UserRepository` gateway (`:model`) -> `JPARepositoryAdapter` -> H2.

## Wiring and conventions

- The `co.com.bancolombia.cleanArchitecture` plugin (5.0.2) supplies Spring Boot/framework versions; module `build.gradle` files only add extra libraries/project refs. Root `build.gradle` intentionally pins Jackson 2.22.3/3.2.3 for CVE fixes.
- `UseCasesConfig` registers plain classes under `co.com.crediya.usecase` whose names end in `UseCase`; keep use cases as plain classes (Lombok is fine) with that package/suffix and no Spring stereotype.
- Servlet stack, not reactive (`reactive=false`).
- API: `POST /api/v1/usuarios`, Spanish snake_case JSON (`correo_electronico`, `salario_base`); requests also accept camelCase aliases. Domain fields differ (`nombre`, `email`); mapping is manual in `ApiRest` (no MapStruct).
- Jackson 3 is intentional: databind types are `tools.jackson.*`; annotations remain `com.fasterxml.jackson.annotation.*`.
- JPA entities must live under `co.com.crediya.jpa` (`JpaConfig` hardcodes `packagesToScan`). H2 in-memory is configured in `application.yaml` (data resets on restart); schema changes come from `hibernate.hbm2ddl.auto=update`—there is no Flyway/Liquibase.
- JPA adapters extend `AdapterOperations` and take the reactivecommons `org.reactivecommons.utils.ObjectMapper` bean from `ObjectMapperConfig`, not Jackson's mapper.
- Domain exceptions mapped to HTTP: `ValidacionDominioException` -> 400 and `CorreoYaRegistradoException` -> 409. A new direct `BusinessException` subtype falls through to the generic 500 unless you add a handler.
- `ArchitectureTest` is marked "Please do not modify this file". It fails on non-final fields in use cases/beans, warns on technology/DTO suffixes in domain names, and writes `build/issues.json`.

## Tests

- JUnit 5 + Mockito. `ApiRestTest` is a full `@SpringBootTest(classes = TestConfig.class)` using `@MockitoBean` for the use case and MockMvc; `TestConfig` excludes `CorsConfig`. No external services are needed.
