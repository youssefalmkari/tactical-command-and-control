plugins {
    id("c2.android.library")
    id("c2.android.hilt")
}

android {
    namespace = "com.example.tacticalcommandandcontrol.core.data"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:common"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.hivemq.mqtt.client)
    implementation(libs.timber)

    testImplementation(libs.junit)
}
