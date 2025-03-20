package com.rabindradev.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UserAuthReq(
    val email: String, val password: String
)

@Serializable
data class CreateAdminRequest(
    val email: String,
    val password: String,
    val name: String,
    val phone: String?,
    val accessCode: String
)