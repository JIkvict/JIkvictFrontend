package org.jikvict.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction


@CacheableTask
abstract class CleanUpSerializableTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        outputDir.convention(inputDir)
    }

    @TaskAction
    fun clean() {
        inputDir
            .get()
            .asFile
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { file ->
                val updatedText =
                    file
                        .readText()
                        .replace("@Serializable@Serializable", "@Serializable")
                        .replace("@KSerializable", "@Serializable")
                        .replace(" : Serializable", "")
                file.writeText(updatedText)
            }
    }
}