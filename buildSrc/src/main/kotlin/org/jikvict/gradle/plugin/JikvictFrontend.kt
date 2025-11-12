package org.jikvict.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.jikvict.gradle.tasks.DeployWasmReleaseTask

abstract class JikvictFrontend : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.compose")
            plugins.apply("extended-openapi")
            plugins.apply("ksp-config")
            plugins.apply("wasm-config")

            tasks.register<DeployWasmReleaseTask>("deployWasmRelease") {
                if (project.findProperty("deploy.version") != null) {
                    version.set(project.property("deploy.version").toString())
                }
                owner.set(project.findProperty("deploy.owner")?.toString() ?: "JIkvict")
                repo.set(project.findProperty("deploy.repo")?.toString() ?: "JIkvictFrontend")
                sshKeyPath.set(
                    project.findProperty("deploy.sshKey")?.toString()
                        ?: (System.getProperty("user.home") + "/.ssh/id_rsa-fiit")
                )
                remoteUser.set(project.findProperty("deploy.remoteUser")?.toString() ?: "ubuntu")
                remoteHost.set(project.findProperty("deploy.remoteHost")?.toString() ?: "147.175.151.161")
                remoteDir.set(project.findProperty("deploy.remoteDir")?.toString() ?: "~/jikvict/frontend/")
            }

        }
    }
}