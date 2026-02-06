plugins {
    id("c2.android.library")
    id("c2.android.hilt")
}

android {
    namespace = "com.example.tacticalcommandandcontrol.core.network"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.hivemq.mqtt.client)
    implementation(libs.mavlink)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
