package com.rabindradev.presentation.plugins

import com.rabindradev.domain.models.UUIDSerializer
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                serializersModule = SerializersModule {
                    contextual(UUIDSerializer)
                }
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
    }
}