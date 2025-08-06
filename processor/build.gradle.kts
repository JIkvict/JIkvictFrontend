plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
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
