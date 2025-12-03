package org.jikvict.browser.util

import java.awt.Desktop
import java.net.URI

actual fun openExternalUrl(url: String, newTab: Boolean) {
    runCatching {
        val desktop = Desktop.getDesktop()
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(URI(url))
        }
    }
}