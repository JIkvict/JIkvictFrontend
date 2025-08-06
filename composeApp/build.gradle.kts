import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    id(libs.plugins.kotlinMultiplatform.get().pluginId)
    id(libs.plugins.androidApplication.get().pluginId) // To delete

    id("jikvict-frontend-plugin")
}

android {
    compileSdk = 36
    namespace = "org.jikvict.composeapp"

    defaultConfig {
        minSdk = 24
        //noinspection OldTargetApi
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

dependencies {
    ksp(project(":processor"))
    debugImplementation(compose.uiTooling)
}

