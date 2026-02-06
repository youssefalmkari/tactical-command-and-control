plugins {
    id("c2.android.library")
    id("c2.android.hilt")
    alias(libs.plugins.room)
}

android {
    namespace = "com.example.tacticalcommandandcontrol.core.database"
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.sqlcipher.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.timber)

    testImplementation(libs.junit)
}
