# ===================================================================
# C2 Tactical Command & Control - ProGuard/R8 Rules
# ===================================================================

# --- HiveMQ MQTT Client ---
-keep class com.hivemq.client.** { *; }
-dontwarn com.hivemq.client.**
-dontwarn io.netty.**

# --- dronefleet MAVLink ---
-keep class io.dronefleet.mavlink.** { *; }
-dontwarn io.dronefleet.mavlink.**

# --- SQLCipher ---
-keep class net.zetetic.database.** { *; }
-dontwarn net.zetetic.database.**

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# --- Hilt ---
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper

# --- Kotlin Serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

# --- OSMDroid ---
-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**

# --- General ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep Timber
-dontwarn org.jetbrains.annotations.**

# Keep data classes used by Room entities
-keepclassmembers class com.example.tacticalcommandandcontrol.core.database.entity.** {
    <fields>;
}
