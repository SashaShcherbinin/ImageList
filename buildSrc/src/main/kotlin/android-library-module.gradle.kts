plugins {
    id("com.android.library")
}

android {
    defaultConfig {
        compileSdk = 36
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
