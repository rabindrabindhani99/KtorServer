package com.rabindradev.presentation.routes

import com.rabindradev.data.models.LoginRequest
import com.rabindradev.data.tables.ErrorLogs
import com.rabindradev.data.tables.insertErrorLog
import com.rabindradev.domain.ErrorParser
import com.rabindradev.domain.ResponseState
import com.rabindradev.domain.models.GeneralResponse
import com.rabindradev.domain.models.LoginResponse
import com.rabindradev.domain.models.SignupResponse
import com.rabindradev.domain.models.UserAuthReq
import com.rabindradev.domain.services.UserService
import com.rabindradev.presentation.plugins.generateJwt
import com.rabindradev.presentation.plugins.getUserEmailFromToken
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userService: UserService) {

    post("/users/signup") {
        val userDto = runCatching { call.receive<UserAuthReq>() }.getOrElse {
            call.respond(HttpStatusCode.BadRequest, GeneralResponse(false, "Invalid request body"))
            return@post
        }

        when (val result = userService.createUser(userDto.email, userDto.password)) {
            is ResponseState.Success -> {
                val token = generateJwt(result.data.id.toString())
                call.respond(HttpStatusCode.Created, ResponseState.Success(SignupResponse(result.data, token)))
            }

            is ResponseState.Error -> {
                ErrorLogs.insertErrorLog(result.message, null, "/signup")
                val parsedMessage = ErrorParser.parse(result.message)
                call.respond(HttpStatusCode.Conflict, GeneralResponse(false, parsedMessage))
            }

            ResponseState.Idle -> {}
            ResponseState.Loading -> {}
        }
    }

    post("/users/login") {
        val loginRequest = runCatching { call.receive<LoginRequest>() }.getOrElse {
            call.respond(HttpStatusCode.BadRequest, GeneralResponse(false, "Invalid request body"))
            return@post
        }

        when (val result = userService.authenticateUser(loginRequest.email, loginRequest.password)) {
            is ResponseState.Success -> {
                val token = generateJwt(result.data.email)
                call.respond(HttpStatusCode.OK, ResponseState.Success(LoginResponse(result.data, token)))
            }

            is ResponseState.Error -> {
                ErrorLogs.insertErrorLog(result.message, null, "/login")
                val parsedMessage = ErrorParser.parse(result.message)
                call.respond(HttpStatusCode.Unauthorized, GeneralResponse(false, parsedMessage))
            }

            ResponseState.Idle -> {}
            ResponseState.Loading -> {}
        }
    }

    authenticate("auth-jwt") {
        post("/users/authorization") {
            val userIdResponse = getUserEmailFromToken(call)
            if (userIdResponse.success) {
                call.respond(HttpStatusCode.OK, ResponseState.Success(userIdResponse))
            } else {
                ErrorLogs.insertErrorLog("You are not authorized", null, "/profile")
                call.respond(HttpStatusCode.Unauthorized, GeneralResponse(false, "You are not authorized"))
            }
        }
    }
}