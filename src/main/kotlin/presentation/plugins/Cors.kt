package com.rabindradev.presentation.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*

fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
}
