package org.jikvict.browser.delegates

import org.jikvict.browser.util.SavableStateFlow
import org.jikvict.browser.viewmodel.ExtendedViewModel
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
inline fun <reified V : Any> ExtendedViewModel.stateHandle(
    key: String,
    default: V,
): SavableStateFlow<V> {
    println("I was called with key: $key and default value: $default")
    return SavableStateFlow(savedStateHandle, key, default, typeOf<V>()).also {
        println("Returning SavableStateFlow for key: $key")
        println("SavableStateFlow : $it")
    }
}
