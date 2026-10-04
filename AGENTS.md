# Repository Guidelines

## Project Structure & Module Organization

- `conventions/` contains common, JVM, Kotlin Multiplatform, Gradle plugin, settings, and root plugins.
- `libraries/` provides core utilities, Gradle extensions, TestKit helpers, and custom KtLint rules.
- Modules separate implementation (`src/main/kotlin`) from public DSL contracts (`src/api/kotlin`, where present).
- Tests live in `src/test/kotlin` and `src/functionalTest/kotlin`; fixture projects live in `src/functionalTest/resources`.
- Shared fixtures use `src/testFixtures/kotlin`.
- ABI snapshots reside in `api/*.api`.
- Dependency versions are centralized in `gradle/libs.versions.toml`.
- Plugin versions are centralized in the `pluginManagement` block of `settings.gradle.kts`.

## Build, Test, and Development Commands

Run commands from the repository root. Use the Gradle wrapper; the daemon and CI use JDK 25.

- `make check`: run standard checks.
- `make test`: run unit tests.
- `make functional-test`: run Gradle TestKit tests.
- `make format`: apply KtLint formatting and sort dependencies.
- `make docs`: generate Dokka API documentation in `build/dokka/html`.
- `make abi`: regenerate Kotlin ABI snapshots after intentional public API changes; review the diff.
- `make publish-local`: publish artifacts to Maven Local for consumer testing.

Pass additional options with `make check GRADLE_ARGS="--info"`.

## Coding Style & Naming Conventions

Follow `.editorconfig`: UTF-8, LF endings, four-space indentation, and a 140-character line limit; YAML uses two spaces.
Kotlin follows IntelliJ KtLint style, trailing commas, and explicit imports without wildcards.
Use PascalCase for classes, camelCase for functions and properties, and lowercase package names under `io.technoirlab`.
Match existing module and plugin names, such as `jvm-conventions` and `io.technoirlab.conventions.jvm-library`.

## Testing Guidelines

Use JUnit Jupiter (JUnit 6), AssertJ, and Gradle TestKit helpers from `libraries/gradle-test-kit`.
Name classes `*Test` or `*FunctionalTest` and use descriptive backtick method names.
Add regression coverage for changed behavior and extend the relevant fixture projects.
Run focused suites with `./gradlew :conventions:jvm-conventions:functionalTest`.
Kover generates coverage reports; no repository-wide percentage threshold is configured.

## Commits and Pull Requests

- Use descriptive branch names without AI harness prefixes (such as `codex/`, `claude/`, `cursor/`, or `junie/`).
- Keep commits focused and use short, imperative commit subjects.
- Do not add a `Co-Authored-By` trailer.
- PR descriptions should explain the problem, the changes made, and the resulting behavior. Include compatibility impacts, remaining limitations, and links to related issues when relevant. Do not include checks performed, validation commands, or validation results.
- Update affected module READMEs and intentional ABI changes alongside code.
