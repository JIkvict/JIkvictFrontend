package org.jikvict.browser.util

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jikvict.api.infrastructure.decodeBase64Bytes
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.dom.Document
import org.w3c.dom.DragEvent
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.files.File
import org.w3c.files.FileList
import org.w3c.files.FileReader
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

actual suspend fun pickFileForUpload(): PickedFile? {
    return try {
        val files = document.selectFilesFromDisk("", false)
        println("Selected files: ${files.joinToString { it.name }}")
        println(files)
        if (files.isEmpty()) {
            null
        } else {
            val file = files.first()
            val bytes = readFileAsByteArray(file)
            PickedFile(name = file.name, bytes = bytes, mimeType = file.type).also {
                println("Picked file: $it")
            }
        }
    } catch (e: Throwable) {
        println("Error picking file: ${e.message}")
        null
    }
}

private suspend fun Document.selectFilesFromDisk(
    accept: String,
    isMultiple: Boolean
): List<File> = suspendCancellableCoroutine { cont ->
    val tempInput = (createElement("input") as HTMLInputElement).apply {
        type = "file"
        style.display = "none"
        this.accept = accept
        multiple = isMultiple
    }

    var focusHandler: ((Event) -> Unit)? = null

    fun cleanup() {
        tempInput.onchange = null
        focusHandler?.let { window.removeEventListener("focus", it) }
        focusHandler = null
        try {
            body?.removeChild(tempInput)
        } catch (_: Throwable) {
        }
    }

    var completed = false

    tempInput.onchange = onchange@{ changeEvt ->
        if (completed) return@onchange
        completed = true
        try {
            val inputElement = changeEvt.target as HTMLInputElement
            val files = inputElement.files?.asList() ?: emptyList()
            cleanup()
            if (cont.isActive) cont.resume(files)
        } catch (e: Throwable) {
            cleanup()
            if (cont.isActive) cont.resumeWithException(e)
        }
    }

    focusHandler = { _: Event ->
        window.setTimeout({
            if (!completed) {
                completed = true
                cleanup()
                if (cont.isActive) cont.resume(emptyList())
            }
            return@setTimeout null
        }, 300)
    }

    window.addEventListener("focus", focusHandler)

    body!!.appendChild(tempInput)
    tempInput.click()

    cont.invokeOnCancellation {
        if (!completed) {
            completed = true
            cleanup()
        }
    }
}

suspend fun readFileAsByteArray(file: File): ByteArray = suspendCancellableCoroutine {
    val reader = FileReader()
    reader.onload = { loadEvt ->
        try {
            val eventFileReader = loadEvt.target?.let { it as FileReader }!!
            val content = eventFileReader.result as ArrayBuffer
            val array = Uint8Array(content)

            val fileByteArray = ByteArray(array.length)
            for (i in 0 until array.length) {
                fileByteArray[i] = array[i]
            }
            it.resumeWith(Result.success(fileByteArray))
        } catch (e: Throwable) {
            it.resumeWithException(e)
        }
    }
    reader.readAsArrayBuffer(file)
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
