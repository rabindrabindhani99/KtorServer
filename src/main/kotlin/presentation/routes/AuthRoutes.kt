package com.rabindradev.presentation.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.rabindradev.data.models.LoginRequest
import com.rabindradev.domain.ResponseState
import com.rabindradev.domain.models.UserDto
import com.rabindradev.domain.services.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.authRoutes(userService: UserService) {
    val jwtSecret = application.environment.config.property("jwt.secret").getString()
    val jwtIssuer = application.environment.config.property("jwt.domain").getString()
    val jwtAudience = application.environment.config.property("jwt.audience").getString()
    val jwtExpiration = 30 * 24 * 60 * 60 * 1000L

    post("/signup") {
        val userDto = runCatching { call.receive<UserDto>() }.getOrElse {
            call.respond(HttpStatusCode.BadRequest, ResponseState.Error("Invalid request body"))
            return@post
        }

        val userResult = runCatching { userService.createUser(userDto.name, userDto.email) }
        userResult.fold(
            onSuccess = { user ->
                val token = generateJwt(user.id.toString(), jwtSecret, jwtIssuer, jwtAudience, jwtExpiration)
                call.respond(HttpStatusCode.Created, mapOf("user" to user, "token" to token))
            },
            onFailure = { exception ->
                when {
                    exception.message?.contains("duplicate key") == true -> {
                        call.respond(HttpStatusCode.Conflict, ResponseState.Error("Email already exists"))
                    }
                    else -> {
                        call.respond(HttpStatusCode.InternalServerError, ResponseState.Error("Failed to create user"))
                    }
                }
            }
        )
    }

    post("/login") {
        val loginRequest = runCatching { call.receive<LoginRequest>() }.getOrElse {
            call.respond(HttpStatusCode.BadRequest, ResponseState.Error("Invalid request body"))
            return@post
        }

        val user = userService.authenticateUser(loginRequest.email, loginRequest.password)
        if (user == null) {
            call.respond(HttpStatusCode.Unauthorized, ResponseState.Error("Invalid credentials"))
            return@post
        }

        val token = generateJwt(user.id.toString(), jwtSecret, jwtIssuer, jwtAudience, jwtExpiration)
        call.respond(HttpStatusCode.OK, mapOf("user" to user, "token" to token))
    }

}
fun generateJwt(subject: String, secret: String, issuer: String, audience: String, expiration: Long): String {
    return JWT.create()
        .withIssuer(issuer)
        .withAudience(audience)
        .withSubject(subject)
        .withExpiresAt(Date(System.currentTimeMillis() + expiration))
        .sign(Algorithm.HMAC256(secret))
}