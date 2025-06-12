package org.jikvict.browser.util

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KType

actual class StateSaver(val savedStateHandle: SavedStateHandle) {
    actual operator fun <T> set(key: String,clazz: KType, value: T?) {
        savedStateHandle[key] = value
    }

    actual operator fun <T : Any> get(key: String, clazz: KType): T? {
        return savedStateHandle[key]
    }

    actual fun <T : Any> asStateFlow(key: String, default: T, clazz: KType): StateFlow<T> {
        return savedStateHandle.getStateFlow(key, default)
    }
}
