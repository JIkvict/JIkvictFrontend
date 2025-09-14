package org.jikvict.browser.auth

import kotlinx.browser.localStorage
import org.jikvict.api.models.TokenResponse

actual var token: TokenResponse?
    get() {
        return localStorage.getItem("token")?.let {
            TokenResponse(it, "Bearer")
        }
    }
    set(value) {
        if (value == null) {
            localStorage.removeItem("token")
        } else {
            localStorage.setItem("token", value.accessToken)
        }
    }