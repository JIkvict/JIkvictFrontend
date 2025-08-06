package org.jikvict.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class JikvictFrontend : Plugin<Project>{
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.compose")
            plugins.apply("extended-openapi")
            plugins.apply("ksp-config")
            plugins.apply("wasm-config")
        }
    }
}