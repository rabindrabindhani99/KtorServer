package com.rabindradev.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.util.UUID

@Serializable
data class User(
    @Contextual val id: UUID, val name: String, val email: String
)
