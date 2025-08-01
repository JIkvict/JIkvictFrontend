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
    alias(libs.plugins.ksp)

    alias(libs.plugins.openApiGenerator)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeHotReload)
}

android {
    compileSdk = 36
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
        @OptIn(ExperimentalKotlinGradlePluginApi::class) compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    } // To delete


    @OptIn(ExperimentalWasmDsl::class) wasmJs {
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
            testTask {
                enabled = false
            }
        }
        compilerOptions {
            freeCompilerArgs.add("-Xwasm-debugger-custom-formatters")
            freeCompilerArgs.add("-Xcontext-parameters")
        }
        binaries.executable()
    }

    sourceSets {

        @OptIn(ExperimentalKotlinGradlePluginApi::class) jvm("desktop") {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_21)
                freeCompilerArgs.add("-Xcontext-parameters")
            }
        }

        @Suppress("unused") val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlin.coroutines.swing)
                implementation(libs.ktor.client.cio)
            }
        }

        @Suppress("unused") val desktopTest by getting

        @Suppress("unused") val wasmJsMain by getting {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }


        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.kotlin.coroutines.android)
            implementation(libs.ktor.client.android)
        } // To delete
        commonMain {
            kotlin.srcDir("${layout.buildDirectory.get()}/generated/openapi/src/commonMain/kotlin")
            kotlin.srcDir("${layout.buildDirectory.get()}/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(libs.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.animation)
                implementation(compose.animationGraphics)
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
                implementation(libs.adaptive)
                implementation(libs.adaptive.layout)
                implementation(libs.adaptive.navigation)
                implementation(libs.material3.adaptive.navigation.suite)
                implementation(libs.material3.window.size.class1)
                implementation(libs.bundles.compottie)
                implementation(libs.ui.util)
                implementation(libs.multiplatform.markdown.renderer)
                implementation(libs.kotlinx.datetime)
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
            "dateLibrary" to "string",
            "serializationLibrary" to "kotlinx_serialization",
            "parcelizeModels" to "false",
            "withJava" to "false",
            "dateTimeFormat" to "iso8601"
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
    compilerOptions.freeCompilerArgs.addAll(listOf("-Xcontext-parameters"))
}
tasks.withType<KotlinCompileCommon>().configureEach {
    dependsOn("openApiGenerate")
    dependsOn("kspCommonMainKotlinMetadata")
    compilerOptions.freeCompilerArgs.addAll(listOf("-Xcontext-parameters"))
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
