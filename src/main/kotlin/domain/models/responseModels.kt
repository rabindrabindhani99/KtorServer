package com.rabindradev.domain.models

import com.rabindradev.data.models.UserDto
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable


@Serializable
data class SignupResponse(
    val user: UserDto, val token: String
)

@Serializable
data class LoginResponse(
    val user: UserDto, val token: String
)

@Serializable
data class UserListResponse(
    val users: List<UserDto>
)

@Serializable
data class GeneralResponse(
    val success: Boolean, val data: String
)

@Serializable
data class AdminResponse(
    val id: String,
    val token: String,
    val email: String,
    val name: String,
    val phone: String?,
    val role: String,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val lastSeen: LocalDateTime?
)