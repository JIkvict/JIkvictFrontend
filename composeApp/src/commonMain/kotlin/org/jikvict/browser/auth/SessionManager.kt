package org.jikvict.browser.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SessionManager {
    private val _isLoggedIn = MutableStateFlow(
        token?.let { true } ?: false
    )
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun logout() {
        _isLoggedIn.value = false
        token = null
    }

    fun login() {
        _isLoggedIn.value = true
    }
}