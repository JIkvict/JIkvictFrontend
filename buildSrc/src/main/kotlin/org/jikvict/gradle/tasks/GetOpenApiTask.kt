package org.jikvict.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jikvict.gradle.tasks.logic.GithubRetriever

abstract class GetOpenApiTask : DefaultTask() {
    @get:Input
    abstract val repoUrl: Property<String>

    @get:Input
    @get:Optional
    abstract val branch: Property<String>

    @get:Input
    @get:Optional
    abstract val version: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        outputFile.convention(project.layout.buildDirectory.file("openapi.json"))
    }

    @TaskAction
    fun execute() {
        val url = repoUrl.get()
        val branchName = branch.orNull
        val ver = version.orNull

        val retriever = GithubRetriever(
            version = ver ?: "latest",
            repoUrl = url,
            branch = branchName ?: "docs"
        )

        outputFile.get().asFile.writeText(retriever.getOpenApiJson())
    }
}
