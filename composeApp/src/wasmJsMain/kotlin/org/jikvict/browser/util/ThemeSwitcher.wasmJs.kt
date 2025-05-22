package org.jikvict.browser.util

import kotlinx.browser.localStorage

actual fun setTheme(isDark: Boolean) {
    localStorage.setItem("theme", if (isDark) "dark" else "light")
}

actual fun getTheme(): Boolean {
    val theme = localStorage.getItem("theme")
    return if (theme == null) {
        localStorage.setItem("theme", "dark")
        true
    } else {
        theme == "dark"
    }
}
