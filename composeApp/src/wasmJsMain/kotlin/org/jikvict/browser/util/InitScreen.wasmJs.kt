package org.jikvict.browser.util

import kotlinx.browser.window
import org.jikvict.browser.screens.NavigableScreen
import org.jikvict.browser.screens.NotFoundScreen
import org.jikvict.browser.screens.routers

actual fun getInitScreen(): NavigableScreen {
    val initRoute =
        window.location.hash
            .substringAfter('#', "")
            .substringBefore("/?")
    val paramsRaw = window.location.hash.substringAfter('?', "")
    val splitParams = paramsRaw.split("&").filter { it.isNotBlank() }
    val params =
        if (splitParams.isEmpty()) {
            emptyMap()
        } else {
            paramsRaw.split("&").associate {
                val (key, value) = it.split("=", limit = 2)
                key to value.ifEmpty { null }
            }
        }
    val router = routers.firstOrNull { it.matchRoute(initRoute) }
    return if (router != null) {
        val screen = router.constructScreen(params)
        screen
    } else {
        NotFoundScreen()
    }
}