# Kotlin Multiplatform ProGuard Rules
# Production-ready configuration for fintech applications

# Keep all public APIs
-keepattributes *Annotation*

# Kotlin specific
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlinx.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class **$$serializer { *; }
-keepclassmembers class ** {
    *** Companion;
}
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**

# Koin
-keep class org.koin.** { *; }
-keep class ** extends org.koin.core.module.Module
-keepclassmembers class ** {
    public <init>(...);
}

# Room Database
-keep class androidx.room.** { *; }
-keep class androidx.sqlite.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# BigNum (Financial calculations)
-keep class com.ionspin.kotlin.bignum.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Application classes - Keep your domain models and business logic
-keep class org.poc.app.**.domain.** { *; }
-keep class org.poc.app.**.data.dto.** { *; }
-keep class org.poc.app.core.domain.** { *; }

# Keep MVI architecture classes
-keep class org.poc.app.core.presentation.mvi.** { *; }
-keep class ** implements org.poc.app.core.presentation.mvi.UiState { *; }
-keep class ** implements org.poc.app.core.presentation.mvi.UiIntent { *; }
-keep class ** implements org.poc.app.core.presentation.mvi.UiSideEffect { *; }

# Keep ViewModels
-keep class ** extends androidx.lifecycle.ViewModel {
    <init>(...);
    public <methods>;
}

# Coil Image Loading
-keep class coil3.** { *; }
-dontwarn coil3.**

# Security - Remove logging in production
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-assumenosideeffects class kotlin.io.ConsoleKt {
    public static void println(...);
}

# Optimization
-allowaccessmodification
-repackageclasses ''