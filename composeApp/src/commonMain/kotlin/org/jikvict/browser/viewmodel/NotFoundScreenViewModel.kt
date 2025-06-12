package org.jikvict.browser.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jikvict.browser.delegates.stateHandle
import org.jikvict.browser.util.StateSaver

class NotFoundScreenViewModel(
    savedStateHandle: StateSaver
) : ExtendedViewModel(savedStateHandle) {
    private val _someType = stateHandle("someType", NotFoundScreenState("Anton", 18))

    val someType = _someType.asStateFlow().onStart {
    }.stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = NotFoundScreenState("Anton", 18))

    private var _timer = stateHandle("timer", 0)

    val timer = _timer.asStateFlow().onStart {
//        startTimer()
    }.stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = 0)


    fun startTimer() {
        viewModelScope.launch {
            println("SomeType is: $_someType")
            for (i in 0..10000) {
                _timer.set(_timer.get() + 1)
                _someType.set(_someType.get().copy(age = _someType.get().age + 1))
                delay(1000L)
            }
        }
    }
}

@Serializable
data class NotFoundScreenState(
    val name: String,
    val age: Int = 0
)
