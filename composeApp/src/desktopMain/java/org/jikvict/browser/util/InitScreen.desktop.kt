package org.jikvict.browser.util

import org.jikvict.browser.screens.MakeJarScreen
import org.jikvict.browser.screens.NavigableScreen

actual fun getInitScreen(): NavigableScreen {
    return MakeJarScreen
}