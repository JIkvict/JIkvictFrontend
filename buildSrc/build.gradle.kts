plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}
kotlin {
    jvmToolchain(22)
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    //noinspection UseTomlInstead
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.9.0.202403050737-r")
    implementation(gradleApi())
    implementation(localGroovy())
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.13.4")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.13.4")
    testImplementation("org.junit.platform:junit-platform-launcher:1.13.4")
}
