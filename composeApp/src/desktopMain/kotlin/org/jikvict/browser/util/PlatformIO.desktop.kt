package org.jikvict.browser.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

actual suspend fun saveBytesAsFile(
    defaultFileName: String,
    bytes: ByteArray,
): Boolean =
    withContext(Dispatchers.IO) {
        try {
            val chooser =
                JFileChooser().apply {
                    selectedFile = File(defaultFileName)
                }
            val result = chooser.showSaveDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                val file = chooser.selectedFile
                file.writeBytes(bytes)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println(e)
            false
        }
    }

actual suspend fun pickFileForUpload(): PickedFile? =
    withContext(Dispatchers.IO) {
        try {
            val chooser =
                JFileChooser().apply {
                    fileFilter = FileNameExtensionFilter("All Files", "zip")
                }
            val result = chooser.showOpenDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                val file = chooser.selectedFile
                PickedFile(
                    name = file.name,
                    bytes = file.readBytes(),
                    mimeType = null,
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

actual fun setupDragAndDropHandlers(
    onDragEnter: () -> Unit,
    onDragLeave: () -> Unit,
    onDragOver: () -> Unit,
    onFileDrop: (List<PickedFile>) -> Unit,
): DragDropHandler? = null
