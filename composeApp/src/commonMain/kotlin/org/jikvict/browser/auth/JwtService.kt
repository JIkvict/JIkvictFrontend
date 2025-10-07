package org.jikvict.browser.auth

import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

object JwtService {
    fun decodeJwtPayload(jwt: String): Jwt {
        val parts = jwt.split(".")
        require(parts.size == 3) { "Invalid JWT" }
        val payload = parts[1]
        val paddedPayload = payload + "=".repeat((4 - payload.length % 4) % 4)
        val standardBase64 = paddedPayload.replace('-', '+').replace('_', '/')

        val decodedString = Base64.decode(standardBase64).decodeToString()
        val decodedJwt = Json.decodeFromString<Jwt>(decodedString)
        return decodedJwt
    }
}