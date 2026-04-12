package org.jikvict.browser.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.io.File

actual suspend fun openJPlagViewerWithReport(fileName: String, bytes: ByteArray) {
    withContext(Dispatchers.IO) {
        runCatching {
            val dir = File(System.getProperty("java.io.tmpdir"), "jikvict-plagiarism")
            dir.mkdirs()
            val file = File(dir, fileName)
            file.writeBytes(bytes)
            runCatching {
                Desktop.getDesktop().open(dir)
            }
        }
        openExternalUrl(JPLAG_VIEWER_URL, true)
    }
}
