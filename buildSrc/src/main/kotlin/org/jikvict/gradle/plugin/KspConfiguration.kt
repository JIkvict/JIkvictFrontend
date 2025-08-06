package org.jikvict.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class KspConfiguration : Plugin<Project> {
    override fun apply(target: Project) {

        target.plugins.apply("com.google.devtools.ksp")

        with(target) {
            afterEvaluate {
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

            }
        }
    }
}