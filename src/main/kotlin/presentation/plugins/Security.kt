package com.rabindradev.presentation.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "ktor sample app"
            verifier(
                JWT.require(Algorithm.HMAC256("your-secret-key"))
                    .withAudience("jwt-audience")
                    .withIssuer("https://jwt-provider-domain/")
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString().isNotEmpty()) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
