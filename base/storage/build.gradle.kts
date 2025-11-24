plugins {
    id(libs.plugins.common.kotlin.library.module)
}

dependencies {
    implementation(libs.kotlin.coroutines.core)
    implementation(projects.core)
    testImplementation(libs.bundles.test.common)
}
