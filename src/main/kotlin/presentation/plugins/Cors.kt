package com.rabindradev.presentation.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)

        allowHost("rabindradev.com", schemes = listOf("http", "https"))
        allowHost("localhost:8080")
        allowHost("0.0.0.0:8081")
    }
}
