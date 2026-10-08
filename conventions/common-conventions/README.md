Common conventions
==================

## Kotlin compiler options

Configures the following compiler options:

- Kotlin language and API versions follow the convention's Kotlin compatibility settings.
- Extra compiler warnings are enabled (`extraWarnings = true`).
- Data-class `copy()` visibility follows the constructor's visibility (`-Xconsistent-data-class-copy-visibility`).
- The unused return value checker is enabled in `check` mode (`-Xreturn-value-checker=check`). It reports warnings for ignored
  results from marked APIs, including Kotlin standard-library functions. Consumers of compiled libraries can leave it disabled.
- `NOTHING_TO_INLINE` warnings are disabled (`-Xwarning-level=NOTHING_TO_INLINE:disabled`).
- JVM targets use `JvmDefaultMode.NO_COMPATIBILITY`.

## Usage

```kotlin
    ...

    metadata {
        // The project's name.
        name = "Example project"
        // The project's description.
        description = "Example project description"
        // The project's URL.
        url = "https://example.org/example-project"

        // The project's developers.
        developer(id = "developer-1", name = "Developer 1", email = "developer-1@example.org")

        // The project's licenses.
        licence(name = "MIT License", url = "http://opensource.org/licenses/MIT", distribution = "repo")
    }
```
