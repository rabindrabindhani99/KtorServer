package com.rabindradev.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val name: String,
    val email: String
)
