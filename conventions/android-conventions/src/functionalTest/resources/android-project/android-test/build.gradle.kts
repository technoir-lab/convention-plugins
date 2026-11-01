plugins {
    id("io.technoirlab.conventions.android-test")
}

androidTest {
    testApplicationId = "com.example.android.test"
    packageName = "com.example.android.test"
    targetProjectPath = ":android-application"
}

dependencies {
    implementation(libs.junit)
}
