package org.jikvict.browser.util

actual suspend fun openJPlagViewerWithReport(fileName: String, bytes: ByteArray) {
    saveBytesAsFile(fileName, bytes)
    openExternalUrl(JPLAG_VIEWER_URL, true)
}
