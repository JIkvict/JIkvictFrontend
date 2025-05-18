import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jikvict.gradle.tasks.CleanUpSerializableTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("org.openapi.generator") version "7.13.0"
    kotlin("plugin.serialization") version "2.1.21"
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                    open = false
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain {
            kotlin.srcDir("${layout.buildDirectory.get()}/generated/openapi/src/commonMain/kotlin")
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.navigation)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.serialization)
                implementation(libs.ktor.json)
                implementation(libs.kotlin.serialization)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
openApiGenerate {
    generatorName.set("kotlin")
    library.set("multiplatform")
    inputSpec.set("$rootDir/openapi.json")
    outputDir.set("${layout.buildDirectory.get()}/generated/openapi")
    packageName.set("org.jikvict.api")
    configOptions.set(
        mapOf(
            "dateLibrary" to "kotlinx-datetime",
            "serializationLibrary" to "kotlinx_serialization",
            "parcelizeModels" to "false",
            "withJava" to "false",
        )
    )
}
tasks.named("openApiGenerate") {
    finalizedBy("cleanUpSerializable")
}
tasks.register<CleanUpSerializableTask>("cleanUpSerializable") {
    group = "build"
    description = "Clean up generated Kotlin files to remove unnecessary annotations and imports"
    inputDir.set(layout.buildDirectory.dir("generated/openapi/src"))
}

tasks.withType<KotlinWebpack>().configureEach {
    dependsOn("openApiGenerate")
}
tasks.named("compileKotlinWasmJs") {
    dependsOn("openApiGenerate")
}
