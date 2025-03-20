package com.rabindradev.data.tables

import com.rabindradev.data.models.UserDto
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import java.util.*

object UsersTable : Table("users") {
    val id = uuid("id").default(UUID.randomUUID())
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    override val primaryKey = PrimaryKey(id)
}

fun UsersTable.insertUser(email: String, password: String): Result<UserDto> {
    return try {
        val userId = UUID.randomUUID()
        insert {
            it[UsersTable.id] = userId
            it[UsersTable.email] = email
            it[UsersTable.password] = password
        }
        Result.success(UserDto(userId, email, password))
    } catch (e: Exception) {
        Result.failure(e)
    }
}