package com.rabindradev.presentation.routes

import com.rabindradev.domain.models.UserDto
import com.rabindradev.domain.services.UserService
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.userRoutes(userService: UserService) {
    route("/users") {
        get {
            call.respond(userService.getAllUsers())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.let { UUID.fromString(it) }
            id?.let {
                userService.getUserById(it)?.let { user -> call.respond(user) }
                    ?: call.respond("User not found")
            } ?: call.respond("Invalid ID")
        }

        post {
            val userDto = call.receive<UserDto>()
            val user = userService.createUser(userDto.name, userDto.email)
            call.respond(user)
        }
    }
}