package com.rabindradev.presentation.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.rabindradev.domain.models.GeneralResponse
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

private lateinit var jwtSecret: String
private lateinit var jwtIssuer: String
private lateinit var jwtAudience: String
private const val jwtExpirationMillis: Long = 30 * 24 * 60 * 60 * 1000L

fun Application.configureSecurity() {
    val config = environment.config
    jwtSecret = config.property("jwt.secret").getString()
    jwtIssuer = config.property("jwt.domain").getString()
    jwtAudience = config.property("jwt.audience").getString()

    install(Authentication) {
        jwt("auth-jwt") {
            authHeader { call ->
                val authHeader = call.request.headers["Authorization"]?.trim()
                if (!authHeader.isNullOrEmpty()) {
                    val token = if (authHeader.startsWith("Bearer ", ignoreCase = true)) {
                        authHeader.removePrefix("Bearer ").trim()
                    } else {
                        authHeader
                    }
                    println("Extracted Token: $token")
                    HttpAuthHeader.Single("Bearer", token)
                } else {
                    null
                }
            }

            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret)).withIssuer(jwtIssuer).withAudience(jwtAudience)
                    .acceptLeeway(10).build()
            )

            validate { credential ->
                val userId = credential.payload.getClaim("sub").asString()
                val isExpired = isJwtExpired(credential.payload.expiresAt)

                if (userId.isNullOrEmpty() || isExpired.success.not()) {
                    println("Token validation failed: ${if (isExpired.success.not()) "Token expired" else "Missing user ID"}")
                    null
                } else {
                    JWTPrincipal(credential.payload)
                }
            }
            challenge { _, _ -> println("JWT Challenge Triggered: Invalid or expired token") }
        }
    }
}

fun generateJwt(subject: String, role: String = "user"): String {
    return JWT.create().withIssuer(jwtIssuer).withAudience(jwtAudience).withSubject(subject).withClaim("role", role)
        .withExpiresAt(Date(System.currentTimeMillis() + jwtExpirationMillis)).sign(Algorithm.HMAC256(jwtSecret))
}

fun isSuperUser(call: ApplicationCall): Boolean {
    val role = call.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
    return role == "superuser"
}

fun getUserEmailFromToken(call: ApplicationCall): GeneralResponse {
    return try {
        val userEmail = call.principal<JWTPrincipal>()?.payload?.getClaim("sub")?.asString()?.trim()
        if (userEmail.isNullOrEmpty()) {
            GeneralResponse(false, "User ID not found in token")
        } else {
            GeneralResponse(true, userEmail)
        }
    } catch (e: Exception) {
        GeneralResponse(false, "Error extracting user ID: ${e.message}")
    }
}

fun isJwtExpired(expiresAt: Date?): GeneralResponse {
    return if (expiresAt == null) {
        GeneralResponse(false, "Token expiration date is missing")
    } else if (expiresAt.before(Date())) {
        GeneralResponse(false, "Token has expired")
    } else {
        GeneralResponse(true, "Token is still valid")
    }
}

fun validateJwtToken(token: String): GeneralResponse {
    return try {
        val algorithm = Algorithm.HMAC256(jwtSecret)
        val verifier = JWT.require(algorithm).withIssuer(jwtIssuer).withAudience(jwtAudience).acceptLeeway(10).build()

        val decodedJwt = verifier.verify(token)

        if (isJwtExpired(decodedJwt.expiresAt).success.not()) {
            GeneralResponse(false, "Token has expired")
        } else {
            GeneralResponse(true, "Token is valid")
        }
    } catch (e: Exception) {
        println("JWT validation failed: ${e.message}")
        GeneralResponse(false, "Invalid token: ${e.message}")
    }
}