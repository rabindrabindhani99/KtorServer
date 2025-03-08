package com.rabindradev.data.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class User(
    @Contextual val id: UUID, val name: String, val email: String
)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class DeviceAuthRequest(val imei: String, val buildNumber: String)
