JVM conventions
===============

## JVM application

```kotlin
plugins {
    id("io.technoirlab.conventions.jvm-application")
}

jvmApplication {
    // The base package name
    packageName = "com.example.jvm.application"

    // The name of the application's main class
    mainClass = ".MainKt"

    // The list of string arguments to pass to the JVM when running the application
    jvmArgs = listOf("-Xmx512m")

    // Optional build features
    buildFeatures {
        // Enable ABI validation
        abiValidation = true
        // Enable Kotlin serialization
        serialization = true

        // Configuration of `BuildConfig` class generation
        buildConfig {
            // Add a String field
            buildConfigField("STRING_FIELD", "string value")
            // Add a variant-specific field
            buildConfigField("TEST_STRING_FIELD", "string value", variant = "test")
        }
    }
}
```

## JVM library

```kotlin
plugins {
    id("io.technoirlab.conventions.jvm-library")
}

jvmLibrary {
    // The base package name
    packageName = "com.example.jvm.library"

    // Optional build features
    buildFeatures {
        // Enable ABI validation
        abiValidation = true
        // Enable Kotlin serialization
        serialization = true

        // Configuration of `BuildConfig` class generation
        buildConfig {
            // Add a String field
            buildConfigField("STRING_FIELD", "string value")
            // Add a variant-specific field
            buildConfigField("TEST_STRING_FIELD", "string value", variant = "test")
        }
    }
}
```
