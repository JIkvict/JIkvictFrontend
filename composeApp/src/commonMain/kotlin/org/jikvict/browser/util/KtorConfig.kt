package org.jikvict.browser.util

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import org.jikvict.api.apis.AuthControllerApi
import org.jikvict.browser.auth.SessionManager
import org.jikvict.browser.auth.token

class ClientConfigProvider(
    val sessionManager: SessionManager,
    val authControllerApi: AuthControllerApi
) {
    fun provide(): (HttpClientConfig<*>) -> Unit {
        return ::clientConfig
    }

    fun clientConfig(client: HttpClientConfig<*>) {
        with(client) {
            expectSuccess = true

            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        token?.let { BearerTokens(it.accessToken, null) }
                    }
                    refreshTokens {
                        val refreshed = authControllerApi.refresh()
                        if (refreshed.success) {
                            val newToken = refreshed.body()
                            token = newToken
                            sessionManager.login()
                            BearerTokens(newToken.accessToken, null)
                        } else {
                            sessionManager.logout()
                            null
                        }
                    }
                }
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
}


fun simpleClientConfig(client: HttpClientConfig<*>) {
    with(client) {
        expectSuccess = true
        install(ContentNegotiation) {
            json()
        }
    }
}