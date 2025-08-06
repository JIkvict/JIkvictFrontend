package org.jikvict.gradle.plugin

import com.google.devtools.ksp.gradle.KspAATask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink

abstract class WasmConfiguration : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.compose")
        target.plugins.apply("org.openapi.generator")
        target.plugins.apply("extended-openapi")
        target.plugins.apply("ksp-config")


        with(target) {
            tasks.withType<KspAATask> {
                dependsOn("openApiGenerate")
            }

            afterEvaluate {
                tasks.withType<KspAATask>().configureEach {
                    if (name != "kspCommonMainKotlinMetadata") {
                        dependsOn("kspCommonMainKotlinMetadata")
                    }
                }

//                tasks.named("kspKotlinWasmJs").configure {
//                    dependsOn("kspCommonMainKotlinMetadata")
//                }
//                tasks.named("kspDebugKotlinAndroid").configure {
//                    dependsOn("kspCommonMainKotlinMetadata")
//                }
//                tasks.named("kspReleaseKotlinAndroid").configure {
//                    dependsOn("kspCommonMainKotlinMetadata")
//                }
//                tasks.withType<KotlinWebpack>().configureEach {
//                    dependsOn("openApiGenerate")
//                }
//                tasks.named("compileKotlinWasmJs") {
//                    dependsOn("openApiGenerate")
//                }


                tasks.withType<KotlinJsIrLink>().configureEach {
                    dependsOn("kspKotlinWasmJs")
                }
            }


        }
    }
}