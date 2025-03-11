package com.rabindradev.data.models

import com.rabindradev.data.tables.UsersTable
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

@Serializable
data class User(
    @Contextual val id: UUID, val name: String, val email: String
)

fun ResultRow.toUser() = User(
    id = this[UsersTable.id],
    name = this[UsersTable.name],
    email = this[UsersTable.email]
)


@Serializable
data class LoginRequest(val email: String, val password: String)