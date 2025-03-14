package com.rabindradev.presentation.plugins

import com.rabindradev.domain.services.UserService
import com.rabindradev.presentation.routes.authRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userService: UserService by inject()
    val prefix = environment.config.propertyOrNull("ktor.routing.prefix")?.getString() ?: ""
    routing {
        route(prefix) {
            authRoutes(userService)
        }
    }
}