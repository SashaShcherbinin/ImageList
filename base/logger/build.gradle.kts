plugins {
    id(libs.plugins.common.android.library.module)
}

android {
    namespace = "base.logger"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core)
    implementation(libs.di.koin.core)
}
