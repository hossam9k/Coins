import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

// Load local.properties for secure API key storage
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

kotlin {
    // Apply expect/actual classes flag globally to all targets
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            // Add explicit bundle ID to avoid compiler warnings
            freeCompilerArgs += listOf(
                "-Xbinary=bundleId=org.poc.app.ComposeApp"
            )
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.android)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.biometric)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.kotlin.date.time)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.bundles.ktor)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.navigation)

            implementation(libs.coil.compose.core)
            implementation(libs.coil.compose)
            implementation(libs.coil.core)
            implementation(libs.coil.svg)
            implementation(libs.coil.network.ktor)

            implementation(libs.bignum)
        }
        iosMain.dependencies {
            implementation(libs.ktor.ios)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlin.test)
            implementation(libs.test.turbine)
            implementation(libs.test.assertk)

            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
    }
}


android {
    namespace = "org.poc.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "org.poc.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    // Build variants for different environments
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            val devApiKey = localProperties.getProperty("DEV_COINCAP_API_KEY")
                ?: System.getenv("DEV_COINCAP_API_KEY")
                ?: ""
            val devApiUrl = localProperties.getProperty("DEV_API_BASE_URL")
                ?: System.getenv("DEV_API_BASE_URL")
                ?: "https://api.coincap.io/v2"

            buildConfigField("String", "ENVIRONMENT", "\"development\"")
            buildConfigField("String", "COINCAP_API_KEY", "\"$devApiKey\"")
            buildConfigField("String", "API_BASE_URL", "\"$devApiUrl\"")
            buildConfigField("boolean", "IS_DEBUG", "true")
            buildConfigField("boolean", "ENABLE_ANALYTICS", "false")
            buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "false")
        }

        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"

            val stagingApiKey = localProperties.getProperty("STAGING_COINCAP_API_KEY")
                ?: System.getenv("STAGING_COINCAP_API_KEY")
                ?: ""
            val stagingApiUrl = localProperties.getProperty("STAGING_API_BASE_URL")
                ?: System.getenv("STAGING_API_BASE_URL")
                ?: "https://api.coincap.io/v2"

            buildConfigField("String", "ENVIRONMENT", "\"staging\"")
            buildConfigField("String", "COINCAP_API_KEY", "\"$stagingApiKey\"")
            buildConfigField("String", "API_BASE_URL", "\"$stagingApiUrl\"")
            buildConfigField("boolean", "IS_DEBUG", "false")
            buildConfigField("boolean", "ENABLE_ANALYTICS", "true")
            buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "true")
        }

        create("prod") {
            dimension = "environment"

            val prodApiKey = localProperties.getProperty("PROD_COINCAP_API_KEY")
                ?: System.getenv("PROD_COINCAP_API_KEY")
                ?: ""
            val prodApiUrl = localProperties.getProperty("PROD_API_BASE_URL")
                ?: System.getenv("PROD_API_BASE_URL")
                ?: "https://api.coincap.io/v2"

            buildConfigField("String", "ENVIRONMENT", "\"production\"")
            buildConfigField("String", "COINCAP_API_KEY", "\"$prodApiKey\"")
            buildConfigField("String", "API_BASE_URL", "\"$prodApiUrl\"")
            buildConfigField("boolean", "IS_DEBUG", "false")
            buildConfigField("boolean", "ENABLE_ANALYTICS", "true")
            buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "true")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // Use target-specific KSP configuration instead of the deprecated general 'ksp'
    add("kspAndroid", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    debugImplementation(compose.uiTooling)
}

// Add task dependencies to ensure proper task ordering
tasks.withType<com.google.devtools.ksp.gradle.KspAATask>().configureEach {
    mustRunAfter(
        tasks.matching { it.name.startsWith("generateResourceAccessorsForAndroid") },
        tasks.matching { it.name.startsWith("generateActualResourceCollectorsForAndroid") },
        tasks.matching { it.name == "generateComposeResClass" },
        tasks.matching { it.name == "generateResourceAccessorsForCommonMain" },
        tasks.matching { it.name == "generateExpectResourceCollectorsForCommonMain" }
    )
}