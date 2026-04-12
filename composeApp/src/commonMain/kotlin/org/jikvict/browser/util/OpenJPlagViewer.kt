package org.jikvict.browser.util

const val JPLAG_VIEWER_URL: String = "https://jplag.github.io/JPlag/"

expect suspend fun openJPlagViewerWithReport(fileName: String, bytes: ByteArray)
