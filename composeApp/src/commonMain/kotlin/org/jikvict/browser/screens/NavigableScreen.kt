package org.jikvict.browser.screens

import androidx.compose.runtime.Composable
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

interface NavigableScreen {
    val compose: @Composable (() -> Unit)

    @OptIn(InternalSerializationApi::class)
    fun getRoute(): String =
        this::class
            .serializer()
            .descriptor.serialName
            .lowercase()
}
