plugins {
    id("com.android.application")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
}

object Version {
    private const val MAJOR = 1
    private const val MINOR = 0
    private const val PATCH = 0
    const val CODE = MAJOR * 10000 + MINOR * 100 + PATCH
    const val NAME = "$MAJOR.$MINOR.$PATCH"
}

android {
    namespace = "app"
    defaultConfig {
        applicationId = "image.list"
        targetSdk = 36
        compileSdk = 36
        minSdk = 23
        versionCode = Version.CODE
        versionName = Version.NAME
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(projects.base.logger)
    implementation(projects.base.network)
    implementation(projects.base.storage)
    implementation(projects.base.compose)
    implementation(projects.core)

    implementation(libs.bundles.koin.android)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.network)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.compose.image.loading.coil)
    implementation(libs.di.koin.android)
    implementation(libs.di.koin.compose)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.datastore.preferences)
    implementation(libs.workmanager)

    debugImplementation(libs.compose.tooling)
}
