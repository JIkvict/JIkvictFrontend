package org.jikvict.browser.util

actual suspend fun saveBytesAsFile(
    defaultFileName: String,
    bytes: ByteArray,
): Boolean {
    // TODO: Implement using SAF or MediaStore as needed
    return false
}

actual suspend fun pickFileForUpload(): PickedFile? {
    // TODO: Implement using ActivityResultContracts.GetContent
    return null
}

actual fun setupDragAndDropHandlers(
    onDragEnter: () -> Unit,
    onDragLeave: () -> Unit,
    onDragOver: () -> Unit,
    onFileDrop: (List<PickedFile>) -> Unit,
): DragDropHandler? = null
