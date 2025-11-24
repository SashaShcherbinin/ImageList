enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

include(":app")
include(":core")
include(":base:compose")
include(":base:network")
include(":base:storage")
include(":base:logger")
