plugins {
    id("c2.android.library")
}

android {
    namespace = "com.example.tacticalcommandandcontrol.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.timber)

    testImplementation(libs.junit)
}
