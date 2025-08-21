package org.jikvict.browser.util

import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jikvict.api.infrastructure.decodeBase64Bytes
import org.khronos.webgl.Uint8Array
import org.w3c.dom.DragEvent
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.files.FileList
import org.w3c.files.FileReader
import kotlin.coroutines.resume

actual suspend fun saveBytesAsFile(
    defaultFileName: String,
    bytes: ByteArray,
): Boolean {
    return try {
        if (bytes.isEmpty()) return false

        val mimeType =
            when {
                defaultFileName.endsWith(".zip") -> "application/zip"
                defaultFileName.endsWith(".pdf") -> "application/pdf"
                defaultFileName.endsWith(".txt") -> "text/plain"
                defaultFileName.endsWith(".json") -> "application/json"
                else -> "application/octet-stream"
            }

        val numbersArray =
            bytes
                .map {
                    it
                        .toUByte()
                        .toUInt()
                        .toInt()
                        .toJsNumber()
                }.toJsArray()
        val uint8Array = Uint8Array(numbersArray)

        val blobParts = JsArray<JsAny?>()
        blobParts[0] = uint8Array

        val blob = Blob(blobParts, BlobPropertyBag(type = mimeType))

        val url = URL.createObjectURL(blob)

        val a = document.createElement("a") as HTMLAnchorElement
        a.href = url
        a.download = defaultFileName
        document.body?.appendChild(a)
        a.click()
        document.body?.removeChild(a)

        URL.revokeObjectURL(url)
        true
    } catch (e: Throwable) {
        println("saveBytesAsFile error: ${e.message}")
        false
    }
}

actual suspend fun pickFileForUpload(): PickedFile? =
    suspendCancellableCoroutine { cont ->
        try {
            val input = document.createElement("input") as HTMLInputElement
            input.type = "file"
            input.style.display = "none"
            document.body?.appendChild(input)

            input.onchange = {
                val file = input.files?.item(0)
                if (file == null) {
                    document.body?.removeChild(input)
                    cont.resume(null)
                } else {
                    val reader = FileReader()
                    reader.onload = {
                        val maybe = reader.result
                        if (maybe == null) {
                            document.body?.removeChild(input)
                            cont.resume(null)
                        } else {
                            val result = maybe.toString()
                            try {
                                val commaIndex = result.indexOf(",")
                                val b64 = if (commaIndex != -1) result.substring(commaIndex + 1) else result
                                val bytes = b64.decodeBase64Bytes()
                                document.body?.removeChild(input)
                                cont.resume(PickedFile(name = file.name, bytes = bytes, mimeType = file.type))
                            } catch (_: Throwable) {
                                document.body?.removeChild(input)
                                cont.resume(null)
                            }
                        }
                    }
                    reader.onerror = {
                        document.body?.removeChild(input)
                        cont.resume(null)
                    }
                    reader.readAsDataURL(file)
                }
            }

            input.click()
        } catch (t: Throwable) {
            cont.resume(null)
        }
    }

actual fun setupDragAndDropHandlers(
    onDragEnter: () -> Unit,
    onDragLeave: () -> Unit,
    onDragOver: () -> Unit,
    onFileDrop: (List<PickedFile>) -> Unit,
): DragDropHandler? =
    try {
        WebDragDropHandler(onDragEnter, onDragLeave, onDragOver, onFileDrop)
    } catch (e: Throwable) {
        println("Failed to setup drag and drop handlers: ${e.message}")
        null
    }

class WebDragDropHandler(
    private val onDragEnter: () -> Unit,
    private val onDragLeave: () -> Unit,
    private val onDragOver: () -> Unit,
    private val onFileDrop: (List<PickedFile>) -> Unit,
) : DragDropHandler {
    private var dragEnterHandler: ((Event) -> Unit)? = null
    private var dragOverHandler: ((Event) -> Unit)? = null
    private var dragLeaveHandler: ((Event) -> Unit)? = null
    private var dropHandler: ((Event) -> Unit)? = null

    init {
        setupEventHandlers()
    }

    private fun setupEventHandlers() {
        dragEnterHandler = { event ->
            event.preventDefault()
            onDragEnter()
        }

        dragOverHandler = { event ->
            event.preventDefault()
            onDragOver()
        }

        dragLeaveHandler = { event ->
            event.preventDefault()
            onDragLeave()
        }

        dropHandler = { event ->
            event.preventDefault()
            val dragEvent = event as? DragEvent
            val files = dragEvent?.dataTransfer?.files
            if (files != null) {
                processDroppedFiles(files)
            }
        }

        document.body?.let { body ->
            dragEnterHandler?.let { body.addEventListener("dragenter", it) }
            dragOverHandler?.let { body.addEventListener("dragover", it) }
            dragLeaveHandler?.let { body.addEventListener("dragleave", it) }
            dropHandler?.let { body.addEventListener("drop", it) }
        }
    }

    private fun processDroppedFiles(fileList: FileList) {
        val pickedFiles = mutableListOf<PickedFile>()
        var processedCount = 0
        val totalFiles = fileList.length

        for (i in 0 until totalFiles) {
            val file = fileList.item(i)
            if (file != null) {
                val reader = FileReader()
                reader.onload = {
                    try {
                        val result = reader.result?.toString()
                        if (result != null) {
                            val commaIndex = result.indexOf(",")
                            val b64 = if (commaIndex != -1) result.substring(commaIndex + 1) else result
                            val bytes = b64.decodeBase64Bytes()
                            pickedFiles.add(PickedFile(name = file.name, bytes = bytes, mimeType = file.type))
                        }
                    } catch (e: Throwable) {
                        println("Error processing dropped file ${file.name}: ${e.message}")
                    }

                    processedCount++
                    if (processedCount == totalFiles) {
                        onFileDrop(pickedFiles)
                    }
                }
                reader.onerror = {
                    println("Error reading dropped file ${file.name}")
                    processedCount++
                    if (processedCount == totalFiles) {
                        onFileDrop(pickedFiles)
                    }
                }
                reader.readAsDataURL(file)
            } else {
                processedCount++
                if (processedCount == totalFiles) {
                    onFileDrop(pickedFiles)
                }
            }
        }

        // Handle case where no files were processed
        if (totalFiles == 0) {
            onFileDrop(emptyList())
        }
    }

    override fun cleanup() {
        document.body?.let { body ->
            dragEnterHandler?.let { body.removeEventListener("dragenter", it) }
            dragOverHandler?.let { body.removeEventListener("dragover", it) }
            dragLeaveHandler?.let { body.removeEventListener("dragleave", it) }
            dropHandler?.let { body.removeEventListener("drop", it) }
        }
        dragEnterHandler = null
        dragOverHandler = null
        dragLeaveHandler = null
        dropHandler = null
    }
}
