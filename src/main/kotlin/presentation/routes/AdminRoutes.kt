package com.rabindradev.presentation.routes

import com.rabindradev.data.tables.ErrorLogs
import com.rabindradev.data.tables.insertErrorLog
import com.rabindradev.domain.ErrorParser
import com.rabindradev.domain.ResponseState
import com.rabindradev.domain.models.CreateAdminRequest
import com.rabindradev.domain.models.GeneralResponse
import com.rabindradev.domain.models.UserListResponse
import com.rabindradev.domain.services.AdminService
import com.rabindradev.domain.services.UserService
import com.rabindradev.presentation.plugins.generateJwt
import com.rabindradev.presentation.plugins.isSuperUser
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.adminRoutes(adminService: AdminService, userService: UserService) {

    // Non-authenticated routes
    post("/admin/access-code/generate") {
        when (val response = adminService.generateAdminAccessCode()) {
            is ResponseState.Success -> call.respond(HttpStatusCode.Created, response)
            is ResponseState.Error -> {
                ErrorLogs.insertErrorLog(response.message, null, "/admin/access-code/generate")
                val parsedMessage = ErrorParser.parse(response.message)
                call.respond(HttpStatusCode.InternalServerError, GeneralResponse(false, parsedMessage))
            }

            is ResponseState.Idle -> {}
            is ResponseState.Loading -> {}
        }
    }

    post("/admin/create") {
        val request = runCatching { call.receive<CreateAdminRequest>() }.getOrElse {
            call.respond(HttpStatusCode.BadRequest, GeneralResponse(false, "Invalid request body"))
            return@post
        }

        when (val accessCodeResponse = adminService.validateAdminAccessCode(request.accessCode)) {
            is ResponseState.Success -> {
                if (accessCodeResponse.data.success) {
                    val token = generateJwt(request.email)
                    when (val createAdminResponse =
                        adminService.createAdmin(request.email, request.password, request.name, request.phone, token)) {
                        is ResponseState.Success -> {
                            adminService.invalidateAdminAccessCode(request.accessCode)
                            call.respond(HttpStatusCode.Created, createAdminResponse)
                        }

                        is ResponseState.Error -> {
                            ErrorLogs.insertErrorLog(createAdminResponse.message, null, "/admin/create")
                            val parsedMessage = ErrorParser.parse(createAdminResponse.message)
                            call.respond(HttpStatusCode.Conflict, GeneralResponse(false, parsedMessage))
                        }

                        is ResponseState.Idle -> {}
                        is ResponseState.Loading -> {}
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized, GeneralResponse(false, "Invalid or expired access code"))
                }
            }

            is ResponseState.Error -> {
                ErrorLogs.insertErrorLog(accessCodeResponse.message, null, "/admin/create")
                val parsedMessage = ErrorParser.parse(accessCodeResponse.message)
                call.respond(HttpStatusCode.Unauthorized, GeneralResponse(false, parsedMessage))
            }

            is ResponseState.Idle -> {}
            is ResponseState.Loading -> {}
        }
    }

    // Authenticated routes
    authenticate("auth-jwt") {
        post("/admin/get") {
            val authHeader = call.request.headers["Authorization"]?.trim()
            val token = if (authHeader != null && authHeader.startsWith("Bearer ", ignoreCase = true)) {
                authHeader.removePrefix("Bearer ").trim()
            } else {
                null
            }

            if (!isSuperUser(call)) {
                call.respond(HttpStatusCode.Forbidden, GeneralResponse(false, "Access denied"))
                return@post
            }

            val request = call.receive<Map<String, String>>()
            val email = request["email"] ?: return@post call.respond(
                HttpStatusCode.BadRequest, GeneralResponse(false, "Email is required")
            )

            when (val adminResponse = adminService.getAdminByEmail(email)) {
                is ResponseState.Success -> call.respond(HttpStatusCode.OK, adminResponse)
                is ResponseState.Error -> {
                    ErrorLogs.insertErrorLog(adminResponse.message, null, "/admin/get", token)
                    val parsedMessage = ErrorParser.parse(adminResponse.message)
                    call.respond(HttpStatusCode.NotFound, GeneralResponse(false, parsedMessage))
                }

                is ResponseState.Idle -> {}
                is ResponseState.Loading -> {}
            }
        }

        post("/admin/all") {

            val authHeader = call.request.headers["Authorization"]?.trim()
            val token = if (authHeader != null && authHeader.startsWith("Bearer ", ignoreCase = true)) {
                authHeader.removePrefix("Bearer ").trim()
            } else {
                null
            }

            if (!isSuperUser(call)) {
                call.respond(HttpStatusCode.Forbidden, GeneralResponse(false, "Access denied"))
                return@post
            }

            when (val adminsResponse = adminService.getAllAdmins()) {
                is ResponseState.Success -> call.respond(HttpStatusCode.OK, adminsResponse)
                is ResponseState.Error -> {
                    ErrorLogs.insertErrorLog(adminsResponse.message, null, "/admin/all", token)
                    val parsedMessage = ErrorParser.parse(adminsResponse.message)
                    call.respond(HttpStatusCode.InternalServerError, GeneralResponse(false, parsedMessage))
                }

                is ResponseState.Idle -> {}
                is ResponseState.Loading -> {}
            }
        }

        post("/admin/last-seen") {

            val authHeader = call.request.headers["Authorization"]?.trim()
            val token = if (authHeader != null && authHeader.startsWith("Bearer ", ignoreCase = true)) {
                authHeader.removePrefix("Bearer ").trim()
            } else {
                null
            }

            if (!isSuperUser(call)) {
                call.respond(HttpStatusCode.Forbidden, GeneralResponse(false, "Access denied"))
                return@post
            }

            val request = call.receive<Map<String, String>>()
            val email = request["email"] ?: return@post call.respond(
                HttpStatusCode.BadRequest, GeneralResponse(false, "Email is required")
            )

            when (val lastSeenResponse = adminService.updateLastSeen(email)) {
                is ResponseState.Success -> call.respond(HttpStatusCode.OK, lastSeenResponse)
                is ResponseState.Error -> {
                    ErrorLogs.insertErrorLog(lastSeenResponse.message, null, "/admin/last-seen", token)
                    val parsedMessage = ErrorParser.parse(lastSeenResponse.message)
                    call.respond(HttpStatusCode.InternalServerError, GeneralResponse(false, parsedMessage))
                }

                is ResponseState.Idle -> {}
                is ResponseState.Loading -> {}
            }
        }

        post("/admin/delete") {
            val authHeader = call.request.headers["Authorization"]?.trim()
            val token = if (authHeader != null && authHeader.startsWith("Bearer ", ignoreCase = true)) {
                authHeader.removePrefix("Bearer ").trim()
            } else {
                null
            }

            if (!isSuperUser(call)) {
                call.respond(HttpStatusCode.Forbidden, GeneralResponse(false, "Access denied"))
                return@post
            }

            val request = call.receive<Map<String, String>>()
            val email = request["email"] ?: return@post call.respond(
                HttpStatusCode.BadRequest, GeneralResponse(false, "Email is required")
            )

            when (val deleteResponse = adminService.deleteAdmin(email)) {
                is ResponseState.Success -> call.respond(HttpStatusCode.OK, deleteResponse)
                is ResponseState.Error -> {
                    ErrorLogs.insertErrorLog(deleteResponse.message, null, "/admin/delete", token)
                    val parsedMessage = ErrorParser.parse(deleteResponse.message)
                    call.respond(HttpStatusCode.NotFound, GeneralResponse(false, parsedMessage))
                }

                is ResponseState.Idle -> {}
                is ResponseState.Loading -> {}
            }
        }

        post("/admin/exists") {
            val authHeader = call.request.headers["Authorization"]?.trim()
            val token = if (authHeader != null && authHeader.startsWith("Bearer ", ignoreCase = true)) {
                authHeader.removePrefix("Bearer ").trim()
            } else {
                null
            }

            if (!isSuperUser(call)) {
                call.respond(HttpStatusCode.Forbidden, GeneralResponse(false, "Access denied"))
                return@post
            }

            val request = call.receive<Map<String, String>>()
            val email = request["email"] ?: return@post call.respond(
                HttpStatusCode.BadRequest, GeneralResponse(false, "Email is required")
            )

            when (val existsResponse = adminService.adminExists(email)) {
                is ResponseState.Success -> call.respond(HttpStatusCode.OK, existsResponse)
                is ResponseState.Error -> {
                    ErrorLogs.insertErrorLog(existsResponse.message, null, "/admin/exists", token)
                    val parsedMessage = ErrorParser.parse(existsResponse.message)
                    call.respond(HttpStatusCode.InternalServerError, GeneralResponse(false, parsedMessage))
                }

                is ResponseState.Idle -> {}
                is ResponseState.Loading -> {}
            }
        }

        post("/users/get-all") {
            val authHeader = call.request.headers["Authorization"]?.trim()
            val token = if (authHeader != null && authHeader.startsWith("Bearer ", ignoreCase = true)) {
                authHeader.removePrefix("Bearer ").trim()
            } else {
                null
            }

            when (val result = userService.getAllUsers()) {
                is ResponseState.Success -> {
                    call.respond(HttpStatusCode.OK, ResponseState.Success(UserListResponse(result.data)))
                }

                is ResponseState.Error -> {
                    ErrorLogs.insertErrorLog(result.message, null, "/users/get-all", token)
                    val parsedMessage = ErrorParser.parse(result.message)
                    call.respond(HttpStatusCode.InternalServerError, GeneralResponse(false, parsedMessage))
                }

                is ResponseState.Idle -> {}
                is ResponseState.Loading -> {}
            }
        }
    }
}