package org.jikvict.browser.util

import kotlinx.browser.localStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KType

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class StateSaver {
    val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = false
        }

    val stateFlows = mutableMapOf<String, MutableStateFlow<Any>>()

    actual fun <T : Any> asStateFlow(
        key: String,
        default: T,
        clazz: KType,
    ): StateFlow<T> {
        @Suppress("UNCHECKED_CAST")
        return stateFlows.getOrPut(key) {
            val initialValue = get(key, clazz) ?: default
            MutableStateFlow(initialValue as Any)
        } as StateFlow<T>
    }

    @Suppress("UNCHECKED_CAST")
    actual operator fun <T : Any> get(
        key: String,
        clazz: KType,
    ): T? {
        val jsonString = localStorage.getItem(key) ?: return null
        return try {
            json.decodeFromString(serializer(clazz), jsonString) as T
        } catch (e: Exception) {
            when {
                jsonString == "true" -> true
                jsonString == "false" -> false
                jsonString.toIntOrNull() != null -> jsonString.toInt()
                jsonString.toLongOrNull() != null -> jsonString.toLong()
                jsonString.toDoubleOrNull() != null -> jsonString.toDouble()
                else -> jsonString
            } as T?
        }
    }

    actual operator fun <T> set(
        key: String,
        clazz: KType,
        value: T?,
    ) {
        if (value == null) {
            return
        }
        try {
            when (value) {
                is String -> localStorage.setItem(key, value)
                is Int, is Long, is Float, is Double, is Boolean -> localStorage.setItem(key, value.toString())
                else -> {
                    try {
                        val jsonString = json.encodeToString(serializer(clazz), value)
                        localStorage.setItem(key, jsonString)
                    } catch (e: Exception) {
                        localStorage.setItem(key, value.toString())
                    }
                }
            }
            stateFlows[key]?.value = value as Any
        } catch (_: Exception) {
        }
    }
}
