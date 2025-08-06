package org.jikvict.browser.util

import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KType

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class StateSaver {
    fun <T : Any> asStateFlow(
        key: String,
        default: T,
        clazz: KType,
    ): StateFlow<T>

    operator fun <T : Any> get(
        key: String,
        clazz: KType,
    ): T?

    operator fun <T> set(
        key: String,
        clazz: KType,
        value: T?,
    )
}
