plugins {
    id(libs.plugins.common.kotlin.library.module)
}

dependencies {
    implementation(libs.di.koin.core)
    implementation(libs.kotlin.coroutines.core)
    testImplementation(libs.bundles.test.common)
}
