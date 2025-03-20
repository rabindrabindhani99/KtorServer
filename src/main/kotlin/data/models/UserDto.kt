package com.rabindradev.data.models

import com.rabindradev.data.tables.UsersTable
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

@Serializable
data class UserDto(
    @Contextual val id: UUID, val email: String, val password: String
)

fun ResultRow.toUser() = UserDto(
    id = this[UsersTable.id], email = this[UsersTable.email], password = this[UsersTable.password]
)

@Serializable
data class LoginRequest(val email: String, val password: String)
