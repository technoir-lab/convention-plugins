Settings conventions
====================

See [common conventions](../common-conventions/README.md) for shared project metadata configuration.

## Usage

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        // Maven repository where the plugins are published
        maven("https://maven.pkg.github.com/technoir-lab/convention-plugins") {
            credentials(PasswordCredentials::class)
        }
    }
    plugins {
        val conventionPluginsVersion = "<version>"
        id("io.technoirlab.conventions.settings") version conventionPluginsVersion
    }
}

plugins {
    id("io.technoirlab.conventions.settings")
}

globalSettings {
    // Unique project ID. Required.
    projectId = "my-project"

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
}
```

## Remote build cache

The convention connects builds to a [GCP Gradle build cache](https://github.com/androidx/gcp-gradle-build-cache) configured through environment variables:

| Variable | Description |
| --- | --- |
| `GCP_BUILD_CACHE_PROJECT_ID` | The ID of the Google Cloud project that owns the bucket. |
| `GCP_BUILD_CACHE_BUCKET_NAME` | The name of the bucket that stores cache entries. |
| `GCP_BUILD_CACHE_CREDENTIALS` | The path to a Google credentials file, such as a service account key or a Workload Identity Federation configuration. |
| `GCP_BUILD_CACHE_PUSH` | `true` to store entries in the remote cache. Otherwise, builds only read from it. |

- Builds use the remote cache only when the project, bucket, and credentials variables are all set. Other builds, including local builds by default, use only the local build cache.
- Builds that push to the remote cache don't use the local build cache.
- The cache doesn't use Application Default Credentials, so other Google Cloud tools in the same build keep their own identity.
- The cache plugin checks access to the bucket when the build starts and fails the build when the credentials can't read it.

The credentials need `storage.buckets.get` on the bucket and `storage.objects.get`, `storage.objects.list`, `storage.objects.create`, and `storage.objects.delete` for its objects. In Technoir Lab repositories, the [`setup-gcp-gradle-build-cache`](https://github.com/technoir-lab/actions/tree/main/setup-gcp-gradle-build-cache) action sets all four variables in GitHub Actions jobs:

```yaml
permissions:
  contents: read
  id-token: write

steps:
  - name: Checkout
    uses: actions/checkout@v7.0.1

  - name: Set up GCP Gradle build cache
    uses: technoir-lab/actions/setup-gcp-gradle-build-cache@v1.2.1
```
