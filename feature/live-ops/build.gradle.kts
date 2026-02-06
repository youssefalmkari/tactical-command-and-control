plugins {
    id("c2.android.feature")
}

android {
    namespace = "com.example.tacticalcommandandcontrol.feature.liveops"
}

dependencies {
    implementation(libs.osmdroid)
}
