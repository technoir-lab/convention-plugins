package io.technoirlab.conventions.common.fixtures

// language=kotlin
val PROJECT_METADATA = """
metadata {
    name = "Example project"
    description = "Example description"
    url = "https://example.org/example-project"

    developer(name = "Example developer 1", email = "example-dev1@example.org")
    developer(
        id = "example-dev2",
        name = "Example developer 2",
        email = "example-dev2@example.org",
        organization = "Example org",
        organizationUrl = "https://example.org"
    )

    license("The Apache Software License, Version 2.0", "https://www.apache.org/licenses/LICENSE-2.0.txt")
    license("MIT License", "http://opensource.org/licenses/MIT", distribution = "repo")
}
""".trimIndent()

// language=xml
val POM_EXPECTED = arrayOf(
    "<name>Example project</name>",
    "<description>Example description</description>",
    "<url>https://example.org/example-project</url>",
    """
    |  <scm>
    |    <connection>scm:git:https://github.com/example-org/example-project.git</connection>
    |    <developerConnection>scm:git:https://github.com/example-org/example-project.git</developerConnection>
    |    <url>https://github.com/example-org/example-project</url>
    |  </scm>
    """.trimMargin(),
    """
    |  <licenses>
    |    <license>
    |      <name>The Apache Software License, Version 2.0</name>
    |      <url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>
    |    </license>
    |    <license>
    |      <name>MIT License</name>
    |      <url>http://opensource.org/licenses/MIT</url>
    |    </license>
    |  </licenses>
    """.trimMargin(),
    """
    |  <developers>
    |    <developer>
    |      <name>Example developer 1</name>
    |      <email>example-dev1@example.org</email>
    |    </developer>
    |    <developer>
    |      <id>example-dev2</id>
    |      <name>Example developer 2</name>
    |      <email>example-dev2@example.org</email>
    |      <organization>Example org</organization>
    |      <organizationUrl>https://example.org</organizationUrl>
    |    </developer>
    |  </developers>
    """.trimMargin()
)
