package com.rabindradev.presentation.routes

import com.rabindradev.domain.ResponseState
import com.rabindradev.domain.models.UserDto
import com.rabindradev.domain.services.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.userRoutes(userService: UserService) {
    route("/users") {
        get {
            val response = userService.getAllUsers()
            call.respond(ResponseState.Success(response))
        }

        get("/{id}") {
            val id = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            if (id == null) {
                call.respond(ResponseState.Error("Invalid ID"))
                return@get
            }

            when (val result = userService.getUserById(id)) {
                null -> call.respond(ResponseState.Error("User not found"))
                else -> call.respond(ResponseState.Success(result))
            }
        }

        post {
            val userDto = runCatching { call.receive<UserDto>() }.getOrElse {
                call.respond(HttpStatusCode.BadRequest, ResponseState.Error("Invalid request body"))
                return@post
            }

            val userResult = runCatching { userService.createUser(userDto.name, userDto.email) }

            userResult.fold(
                onSuccess = { user ->
                    call.respond(HttpStatusCode.Created, ResponseState.Success(user))
                },
                onFailure = { exception ->
                    when {
                        exception.message?.contains("duplicate key value violates unique constraint") == true -> {
                            call.respond(HttpStatusCode.Conflict, ResponseState.Error("Email already exists"))
                        }
                        else -> {
                            call.respond(HttpStatusCode.InternalServerError, ResponseState.Error("Failed to create user"))
                        }
                    }
                }
            )
        }

    }
}
