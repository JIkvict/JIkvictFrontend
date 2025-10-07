package org.jikvict.browser.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager {
    private val _isLoggedIn = MutableStateFlow(
        TokenHolder.token()?.let {
            true
        } ?: false
    )
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun logout() {
        _isLoggedIn.value = false
        TokenHolder.setToken(null)
        println("logged out")
    }

    fun login() {
        _isLoggedIn.value = true
    }
}