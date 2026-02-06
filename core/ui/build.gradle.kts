plugins {
    id("c2.android.library")
    id("c2.android.compose")
}

android {
    namespace = "com.example.tacticalcommandandcontrol.core.ui"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))

    implementation(libs.osmdroid)
    implementation(libs.timber)
}
