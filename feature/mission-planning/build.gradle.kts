plugins {
    id("c2.android.feature")
}

android {
    namespace = "com.example.tacticalcommandandcontrol.feature.missionplanning"
}

dependencies {
    implementation(libs.osmdroid)
}
