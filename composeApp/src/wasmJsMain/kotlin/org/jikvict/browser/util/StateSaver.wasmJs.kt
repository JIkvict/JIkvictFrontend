package org.jikvict.browser.util

import kotlinx.browser.localStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json

actual class StateSaver {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
    }

    val stateFlows = mutableMapOf<String, MutableStateFlow<Any>>()

    actual inline fun <reified T> asStateFlow(key: String, default: T): StateFlow<T> {
        @Suppress("UNCHECKED_CAST")
        return stateFlows.getOrPut(key) {
            val initialValue = get<T>(key) ?: default
            MutableStateFlow(initialValue as Any)
        } as StateFlow<T>
    }

    actual inline operator fun <reified T> get(key: String): T? {
        val jsonString = localStorage.getItem(key) ?: return null
        return try {
            @Suppress("UNCHECKED_CAST")
            json.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    actual operator fun <T> set(key: String, value: T?) {
        if (value == null) {
            localStorage.removeItem(key)
            stateFlows[key]?.value = null as Any
        } else {
            try {
                val jsonString = json.encodeToString(value)
                localStorage.setItem(key, jsonString)
                @Suppress("UNCHECKED_CAST")
                stateFlows[key]?.value = value as Any
            } catch (e: Exception) {
            }
        }
    }
}
