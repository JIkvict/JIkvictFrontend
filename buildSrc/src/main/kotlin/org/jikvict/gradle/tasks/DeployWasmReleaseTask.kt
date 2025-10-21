package org.jikvict.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream
import kotlin.io.path.Path

/**
 * Downloads the wasmJs distribution zip from GitHub Releases by tag and deploys it to a remote server via scp.
 *
 * Usage examples:
 *  ./gradlew deployWasmRelease -Pdeploy.version=1.2.3
 *  ./gradlew deployWasmRelease -Pdeploy.version=v1.2.3 -Pdeploy.remoteHost=147.175.151.161 -Pdeploy.remoteUser=ubuntu \
 *      -Pdeploy.remoteDir=~/jikvict/frontend -Pdeploy.sshKey=~/.ssh/id_rsa-fiit
 */
abstract class DeployWasmReleaseTask : DefaultTask() {
    @get:Input
    abstract val version: Property<String> // e.g., 1.2.3 or v1.2.3

    @get:Input
    @get:Optional
    abstract val owner: Property<String> // default: JIkvict

    @get:Input
    @get:Optional
    abstract val repo: Property<String> // default: JIkvictFrontend

    @get:Input
    @get:Optional
    abstract val sshKeyPath: Property<String> // default: ~/.ssh/id_rsa-fiit

    @get:Input
    @get:Optional
    abstract val remoteUser: Property<String> // default: ubuntu

    @get:Input
    @get:Optional
    abstract val remoteHost: Property<String> // default: 147.175.151.161

    @get:Input
    @get:Optional
    abstract val remoteDir: Property<String> // default: ~/jikvict/frontend/

    private fun normalizeTag(ver: String): String = if (ver.startsWith("v")) ver.removePrefix("v") else ver

    private fun buildAssetUrl(tag: String, owner: String, repo: String): String {
        val assetName = "dist-wasmJs-$tag.zip"
        return "https://github.com/$owner/$repo/releases/download/$tag/$assetName"
    }

    private fun download(url: String, target: File) {
        target.parentFile.mkdirs()
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.instanceFollowRedirects = true
        connection.connectTimeout = 30_000
        connection.readTimeout = 60_000
        connection.requestMethod = "GET"
        connection.inputStream.use { input ->
            BufferedInputStream(input).use { bis ->
                FileOutputStream(target).use { fos ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var read = bis.read(buffer)
                    while (read >= 0) {
                        if (read > 0) fos.write(buffer, 0, read)
                        read = bis.read(buffer)
                    }
                    fos.flush()
                }
            }
        }
    }

    private fun unzip(zipFile: File, targetDir: File) {
        if (targetDir.exists()) targetDir.deleteRecursively()
        targetDir.mkdirs()
        ZipInputStream(zipFile.inputStream().buffered()).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val outFile = File(targetDir, entry.name)
                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    outFile.parentFile?.mkdirs()
                    FileOutputStream(outFile).use { fos ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var read = zis.read(buffer)
                        while (read > 0) {
                            fos.write(buffer, 0, read)
                            read = zis.read(buffer)
                        }
                        fos.flush()
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }

    @TaskAction
    fun run() {
        val tag = normalizeTag(version.get())
        val ownerVal = owner.orNull ?: "JIkvict"
        val repoVal = repo.orNull ?: "JIkvictFrontend"

        val url = buildAssetUrl(tag, ownerVal, repoVal)

        val buildDir = project.layout.buildDirectory.get().asFile
        val downloadDir = File(buildDir, "deploy/download")
        val zipFile = File(downloadDir, "dist-wasmJs-$tag.zip")

        logger.lifecycle("Downloading release asset from $url ...")
        try {
            download(url, zipFile)
        } catch (e: Exception) {
            throw RuntimeException("Failed to download release asset. Make sure the tag and asset exist: $url", e)
        }

        val distDir = project.layout.projectDirectory.dir("composeApp/build/dist/wasmJs/productionExecutable").asFile
        logger.lifecycle("Unpacking asset to ${distDir.absolutePath} ...")
        unzip(zipFile, distDir)

        val sshKey = sshKeyPath.orNull ?: "~/.ssh/id_rsa-fiit"
        val user = remoteUser.orNull ?: "ubuntu"
        val host = remoteHost.orNull ?: "147.175.151.161"
        val remote = remoteDir.orNull ?: "~/jikvict/frontend/"

        if (!File(sshKey).exists()) {
            throw IllegalStateException("SSH key not found at $sshKey. Provide -Pdeploy.sshKey=... if needed.")
        }
        if (!distDir.exists() || distDir.listFiles().isNullOrEmpty()) {
            throw IllegalStateException("Distribution directory is empty: ${distDir.absolutePath}")
        }

        // Important: use shell to expand the wildcard to copy directory contents
        val cmd =
            "scp -i ${shellEscape(sshKey)} -o StrictHostKeyChecking=no -r ${shellEscape(distDir.absolutePath)}/. ${
                shellEscape("$user@$host:$remote")
            }"
        logger.lifecycle("Executing: $cmd")
        project.exec {
            commandLine("sh", "-c", cmd)
        }
        logger.lifecycle("Deployment completed for tag $tag")
    }

    private fun shellEscape(s: String): String {
        // Basic escaping by wrapping with single quotes and escaping existing single quotes
        if (s.isEmpty()) return "''"
        return "'" + s.replace("'", "'\\''") + "'"
    }
}
