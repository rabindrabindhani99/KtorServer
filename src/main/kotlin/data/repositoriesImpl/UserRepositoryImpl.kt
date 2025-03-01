package com.rabindradev.data.repositoriesImpl

import com.rabindradev.data.models.User
import com.rabindradev.data.repositories.UserRepository
import com.rabindradev.data.tables.UsersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class UserRepositoryImpl(private val db: Database) : UserRepository {

    override suspend fun getAllUsers(): List<User> = newSuspendedTransaction(db = db) {
        UsersTable.selectAll().map {
            User(
                id = it[UsersTable.id],
                name = it[UsersTable.name],
                email = it[UsersTable.email]
            )
        }
    }

    override suspend fun getUserById(id: UUID): User? = newSuspendedTransaction(db = db) {
        UsersTable.select { UsersTable.id eq id }
            .map {
                User(
                    id = it[UsersTable.id],
                    name = it[UsersTable.name],
                    email = it[UsersTable.email]
                )
            }
            .singleOrNull()
    }

    override suspend fun createUser(name: String, email: String): User = newSuspendedTransaction(db = db) {
        val id = UUID.randomUUID()
        UsersTable.insert {
            it[UsersTable.id] = id
            it[UsersTable.name] = name
            it[UsersTable.email] = email
        }
        User(id, name, email)
    }
}