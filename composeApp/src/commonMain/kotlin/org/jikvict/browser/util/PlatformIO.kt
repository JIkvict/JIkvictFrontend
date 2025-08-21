package org.jikvict.browser.util

data class PickedFile(
    val name: String,
    val bytes: ByteArray,
    val mimeType: String? = null,
)

expect suspend fun saveBytesAsFile(defaultFileName: String, bytes: ByteArray): Boolean

expect suspend fun pickFileForUpload(): PickedFile?

expect fun setupDragAndDropHandlers(
    onDragEnter: () -> Unit = {},
    onDragLeave: () -> Unit = {},
    onDragOver: () -> Unit = {},
    onFileDrop: (List<PickedFile>) -> Unit = {}
): DragDropHandler?

interface DragDropHandler {
    fun cleanup()
}
