package com.rabindradev.presentation.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.RSAKeyProvider
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.math.BigInteger
import java.net.URL
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.Base64

fun Application.configureSecurity() {
    val config = environment.config
    val firebaseIssuer = config.property("jwt.domain").getString()
    val firebaseAudience = config.property("jwt.audience").getString()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = config.property("jwt.realm").getString()

            verifier(
                JWT.require(Algorithm.RSA256(FirebaseKeyProvider(this@configureSecurity))).withIssuer(firebaseIssuer)
                    .withAudience(firebaseAudience).build()
            )

            validate { credential ->
                val userId = credential.payload.getClaim("sub").asString()
                if (!userId.isNullOrEmpty()) JWTPrincipal(credential.payload) else null
            }
        }
    }
}

@Serializable
data class JwkKey(val kid: String, val n: String, val e: String)

@Serializable
data class JwkKeys(val keys: List<JwkKey>)

class FirebaseKeyProvider(application: Application) : RSAKeyProvider {
    private val jwksUrl = application.environment.config.property("jwt.jwks_url").getString()

    override fun getPublicKeyById(keyId: String?): RSAPublicKey {
        require(!keyId.isNullOrEmpty()) { "Missing Key ID in JWT Header" }

        val jsonText = URL(jwksUrl).readText()
        val keySet = Json.decodeFromString<JwkKeys>(jsonText)

        val matchingKey = keySet.keys.find { it.kid == keyId }
            ?: throw IllegalArgumentException("Firebase public key with ID $keyId not found.")

        return decodePublicKey(matchingKey)
    }

    private fun decodePublicKey(jwk: JwkKey): RSAPublicKey {
        val nBytes = Base64.getUrlDecoder().decode(jwk.n)
        val eBytes = Base64.getUrlDecoder().decode(jwk.e)

        val modulus = BigInteger(1, nBytes)
        val exponent = BigInteger(1, eBytes)

        val keyFactory = KeyFactory.getInstance("RSA")
        val spec = RSAPublicKeySpec(modulus, exponent)
        return keyFactory.generatePublic(spec) as RSAPublicKey
    }

    override fun getPrivateKey(): RSAPrivateKey? = null
    override fun getPrivateKeyId(): String? = null
}