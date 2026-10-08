Android conventions
===================

See [common conventions](../common-conventions/README.md#kotlin-compiler-options) for shared Kotlin compiler options.

## Android application

```kotlin
plugins {
    id("io.technoirlab.conventions.android-application")
}

androidApplication {
    packageName = "com.example.android.application"
    applicationId = "com.example.android.application"
    versionCode = 1
    versionName = "1.0"

    buildFeatures {
        parcelize = true
        serialization = true

        buildConfig {
            buildConfigField("API_URL", "https://example.com")
            buildConfigField("DEBUG_API_URL", "https://debug.example.com", variant = "debug")
        }
    }
}
```

`applicationId` is required. `versionName` defaults to `project.version`; the SDK and version defaults can be overridden through the convention extension.

## Android library

```kotlin
plugins {
    id("io.technoirlab.conventions.android-library")
}

androidLibrary {
    packageName = "com.example.android.library"

    buildFeatures {
        parcelize = true
        serialization = true
        testFixtures = true

        buildConfig {
            buildConfigField("LIBRARY_NAME", "example")
            buildConfigField("RELEASE_MODE", true, variant = "release")
        }
    }
}
```

Android libraries publish the release AAR with source and Dokka documentation artifacts. Android test fixtures are opt-in and are published with the release variant.

Local unit tests use JUnit Jupiter and include Kover coverage tasks. `packageName` defaults to the project name with hyphens and underscores replaced by dots.

Parcelize and serialization support are opt-in. Enabling Parcelize only applies the Kotlin Parcelize compiler plugin.

BuildConfig fields use Android's native Java generator and support strings, booleans, integers, longs, floats, and doubles. A variant name addresses an Android build type.

Android Multiplatform, ABI validation, Compose, product flavors, signing, baseline profiles, and executing instrumented tests are not configured by these plugins.
