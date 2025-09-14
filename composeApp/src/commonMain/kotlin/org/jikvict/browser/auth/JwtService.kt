package org.jikvict.browser.auth

import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

object JwtService {
    fun decodeJwtPayload(jwt: String): Jwt {
        val parts = jwt.split(".")
        require(parts.size == 3) { "Invalid JWT" }
        val payload = parts[1]
        val decodedString = Base64.decode(payload).decodeToString()
        val decodedJwt = Json.decodeFromString<Jwt>(decodedString)
        return decodedJwt
    }
}
