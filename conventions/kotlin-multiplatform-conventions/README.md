Kotlin multiplatform conventions
================================

## KMP Application

```kotlin
plugins {
    id("io.technoirlab.conventions.kotlin-multiplatform-application")
}

kotlinMultiplatformApplication {
    // The base package name
    packageName = "com.example.kmp.application"

    // Optional build features
    buildFeatures {
        // Enable ABI validation. Disabled by default.
        abiValidation = true
        // Enable benchmarking. Disabled by default.
        benchmark = true
        // Enable Metro dependency injection. Disabled by default.
        metro = true
        // Enable `toString()` redaction. Disabled by default.
        redacted = true
        // Enable Kotlin serialization. Disabled by default.
        serialization = true
        // Enable C interop. Disabled by default.
        cinterop = true

        // Configuration of `BuildConfig` class generation
        buildConfig {
            // Add a String field
            buildConfigField("STRING_FIELD", "string value")
            // Add a variant-specific field
            buildConfigField("TEST_STRING_FIELD", "string value", variant = "test")
        }
    }
}

kotlin {
    // Define the targets
    jvm()
    ...
}
```

### Binaries

For the targets declared in `kotlin { ... }`, the plugin registers:

* Apple targets (macOS, iOS, tvOS, and watchOS): debug and release static frameworks with the `static` name prefix
  (`staticDebugFramework` and `staticReleaseFramework`). The framework base name is the project name.
* Desktop native targets (macOS, Linux, and Windows): debug and release executables
  (`debugExecutable` and `releaseExecutable`), with `runDebugExecutable` and `runReleaseExecutable` tasks for the host target.
  The entry point is `main` in the configured `packageName`.
* Android native targets: debug and release shared libraries (`debugShared` and `releaseShared`) for embedding in Android applications.
* Wasm/JS targets: executable binaries.
* Other targets: no additional production binaries.

## KMP Library

```kotlin
plugins {
    id("io.technoirlab.conventions.kotlin-multiplatform-library")
}

kotlinLibrary {
    // The base package name
    packageName = "com.example.kmp.library"

    // Optional build features
    buildFeatures {
        // Enable ABI validation. Disabled by default.
        abiValidation = true
        // Enable benchmarking. Disabled by default.
        benchmark = true
        // Enable Metro dependency injection. Disabled by default.
        metro = true
        // Enable `toString()` redaction. Disabled by default.
        redacted = true
        // Enable Kotlin serialization. Disabled by default.
        serialization = true
        // Enable C interop. Disabled by default.
        cinterop = true

        // Configuration of `BuildConfig` class generation
        buildConfig {
            // Add a String field
            buildConfigField("STRING_FIELD", "string value")
            // Add a variant-specific field
            buildConfigField("TEST_STRING_FIELD", "string value", variant = "test")
        }
    }
}

kotlin {
    // Define the targets
    jvm()
    ...
}
```
