package org.jikvict.browser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotFoundScreenViewModel : ViewModel() {


    private val _timer = MutableStateFlow(0)
    val timer = _timer.asStateFlow()

    fun startTimer() {
        viewModelScope.launch {
            for (i in 0..100) {
                _timer.value = i
                kotlinx.coroutines.delay(1000L)
            }
        }
    }

    init {
        startTimer()
    }
}

