package org.jikvict.browser.util

import io.ktor.client.HttpClientConfig

fun clientConfig(client: HttpClientConfig<*>) {
    with(client) {
        expectSuccess = true
    }
}
