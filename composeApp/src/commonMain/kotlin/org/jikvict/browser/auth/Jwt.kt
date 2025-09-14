package org.jikvict.browser.auth

import kotlinx.serialization.Serializable
import org.jikvict.api.models.TokenResponse

@Serializable
class Jwt {
    val sub: String = ""
    val roles: List<String> = emptyList()
    val iat: Long = 0L
    val exp: Long = 0L
    override fun toString(): String {
        return "Jwt(sub='$sub', roles=$roles, iat=$iat, exp=$exp)"
    }
}

fun TokenResponse?.toJwt(): Jwt? {
    return this?.accessToken?.let {
        JwtService.decodeJwtPayload(it)
    }
}