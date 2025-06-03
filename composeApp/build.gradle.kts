import com.google.devtools.ksp.gradle.KspAATask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon
import org.jikvict.gradle.tasks.CleanUpSerializableTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidApplication) // To delete
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"

    id("org.openapi.generator") version "7.13.0"
    kotlin("plugin.serialization") version "2.1.21"
    id("org.jetbrains.compose.hot-reload") version "1.0.0-alpha10"
}

android {
    compileSdk = 35
    namespace = "org.jikvict.composeapp"

    defaultConfig {
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    sourceSets["main"].manifest.srcFile("src/commonMain/AndroidManifest.xml")
} // To delete
kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    } // To delete

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer =
                    (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                        static =
                            (static ?: mutableListOf()).apply {
                                add(rootDirPath)
                                add(projectDirPath)
                            }
                        open = false
                    }
            }
            testTask {
                enabled = false
            }
        }
        compilerOptions {
            freeCompilerArgs.add("-Xwasm-debugger-custom-formatters")
        }
        binaries.executable()
    }

    sourceSets {

        jvm("desktop") {
            compilations.all {
                kotlinOptions.jvmTarget = "21"
            }
        }

        sourceSets {
            val desktopMain by getting {
                dependencies {
                    implementation(compose.desktop.currentOs)
                }
            }
            val desktopTest by getting
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        } // To delete
        commonMain {
            kotlin.srcDir("${layout.buildDirectory.get()}/generated/openapi/src/commonMain/kotlin")
            kotlin.srcDir("${layout.buildDirectory.get()}/generated/ksp/metadata/commonMain/kotlin")
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
                implementation(libs.tooling.preview)
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.bundles.koin)
                implementation(libs.kotlin.coroutines)
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
            "withJava" to "false"
        )
    )
}

dependencies {
    ksp(project(":processor"))
    debugImplementation(compose.uiTooling)
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

tasks.withType<KotlinCompile>().configureEach {
    dependsOn("openApiGenerate")
    dependsOn("kspCommonMainKotlinMetadata")
}
tasks.withType<KotlinCompileCommon>().configureEach {
    dependsOn("openApiGenerate")
    dependsOn("kspCommonMainKotlinMetadata")
}
tasks.withType<KspAATask> {
    dependsOn("openApiGenerate")
}

afterEvaluate {
    tasks.withType<KspAATask>().configureEach {
        if (name != "kspCommonMainKotlinMetadata") {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }

    tasks.named("kspKotlinWasmJs").configure {
        dependsOn("kspCommonMainKotlinMetadata")
    }
    tasks.named("kspDebugKotlinAndroid").configure {
        dependsOn("kspCommonMainKotlinMetadata")
    }
    tasks.named("kspReleaseKotlinAndroid").configure {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink>().configureEach {
    dependsOn("kspKotlinWasmJs")
}

tasks.matching { it.name.startsWith("kspReleaseKotlinAndroid") }.configureEach {
    enabled = false
}
tasks.matching { it.name.startsWith("kspDebugKotlinAndroid") }.configureEach {
    enabled = false
}
tasks.matching { it.name.startsWith("kspKotlinWasmJs") }.configureEach {
    enabled = false
}

tasks.matching { it.name.startsWith("kspCommonMain") }.configureEach {
    enabled = true
}