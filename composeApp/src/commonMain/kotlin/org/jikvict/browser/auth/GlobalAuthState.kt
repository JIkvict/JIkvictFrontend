package org.jikvict.browser.auth

import androidx.compose.runtime.mutableStateOf
import org.jikvict.api.models.TokenResponse


object TokenHolder {
    var tokenVersion = mutableStateOf(0)
    fun token() = token
    fun setToken(newToken: TokenResponse?) {
        token = newToken
        tokenVersion.value++
    }
}
expect var token: TokenResponse?
