plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
}

group = "org.jikvict"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {

    implementation(libs.symbol.processing.api)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(22)
}
