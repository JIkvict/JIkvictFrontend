plugins {
    alias(libs.plugins.androidApplication) apply false // To delete
    alias(libs.plugins.androidLibrary) apply false // To delete
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    kotlin("jvm") version "2.2.0"
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(22)
}
