package org.jikvict.browser.screens

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

interface ScreenRouterRegistrar<T : NavigableScreen> {
    val screen: KClass<T>

    @OptIn(InternalSerializationApi::class)
    fun matchRoute(route: String): Boolean =
        route ==
            screen
                .serializer()
                .descriptor
                .serialName
                .lowercase()

    fun constructScreen(params: Map<String, String?>): NavigableScreen
}
