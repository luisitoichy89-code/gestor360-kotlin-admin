# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keep class org.jetbrains.** { *; }

# Supabase
-keep class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }

# Serialization
-keep class * implements kotlinx.serialization.KSerializer { *; }
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# Compose
-keep class androidx.compose.** { *; }

# Proguard optimizaciones (opcional)
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontwarn kotlinx.serialization.AnnotationsKt
-dontwarn org.jetbrains.annotations.**
