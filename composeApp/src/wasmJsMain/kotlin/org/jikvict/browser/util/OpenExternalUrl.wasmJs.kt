package org.jikvict.browser.util

import kotlinx.browser.window

actual fun openExternalUrl(url: String, newTab: Boolean) {
    window.open(url, if (newTab) "_blank" else "_self")
}