package com.rabindradev.data.tables

import com.rabindradev.data.models.User
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.util.UUID

object UsersTable : Table("users") {
    val id = uuid("id").default(UUID.randomUUID())
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}

fun UsersTable.insertUser(name: String, email: String): Result<User> {
    return try {
        val id = UUID.randomUUID()
        insert {
            it[UsersTable.id] = id
            it[UsersTable.name] = name
            it[UsersTable.email] = email
        }
        Result.success(User(id, name, email))
    } catch (e: Exception) {
        Result.failure(e)
    }
}


fun Transaction.insertUserRaw(name: String, email: String): Result<User> {
    return try {
        val id = UUID.randomUUID().toString()
        val query = "INSERT INTO users (id, name, email) VALUES ('$id', '$name', '$email')"

        exec(query)

        Result.success(User(UUID.fromString(id), name, email))
    } catch (e: Exception) {
        Result.failure(e)
    }
}


