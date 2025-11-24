plugins {
    id(libs.plugins.common.android.library.module)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "base.compose"
    resourcePrefix = "base_compose"
}

dependencies {
    implementation(libs.compose.material)
    implementation(libs.compose.preview)
    implementation(libs.compose.navigation)
    implementation(libs.compose.systemuicontroller)
}
