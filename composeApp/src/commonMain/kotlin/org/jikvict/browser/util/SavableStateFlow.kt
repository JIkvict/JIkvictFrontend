package org.jikvict.browser.util

import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KType

data class SavableStateFlow<T : Any>(
    private val stateSaver: StateSaver,
    private val key: String,
    private val default: T,
    private val type: KType,
) {
    fun set(value: T) {
        stateSaver[key, type] = value
    }

    fun asStateFlow(): StateFlow<T> = stateSaver.asStateFlow(key = key, default = default, type)

    fun get(): T = stateSaver[key, type] ?: default
}
