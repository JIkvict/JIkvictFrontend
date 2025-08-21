package org.jikvict.browser.util

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json

fun clientConfig(client: HttpClientConfig<*>) {
    with(client) {
        expectSuccess = true

        install(ContentNegotiation) {
            json()
        }

        install(
            createClientPlugin("FixMultipartContentType") {
                onRequest { request, _ ->
                    val body = request.body
                    if (body is MultiPartFormDataContent) {
                        request.headers.remove(HttpHeaders.ContentType)
                    }
                }
            },
        )
    }
}