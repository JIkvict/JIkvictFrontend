package org.jikvict.browser.util

@Suppress(names = ["unused"])
actual fun log(message: String): Unit = js("console.log(message)")
