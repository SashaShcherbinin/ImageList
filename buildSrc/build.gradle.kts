plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    alias(libs.plugins.kotlin.serialization)
}
repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.android)
    implementation(libs.gradle.kotlin)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.bundles.network)
}
