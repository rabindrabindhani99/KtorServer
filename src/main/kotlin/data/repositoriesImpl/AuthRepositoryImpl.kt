package com.rabindradev.data.repositoriesImpl

import com.rabindradev.data.models.User
import com.rabindradev.data.models.toUser
import com.rabindradev.data.repositories.AuthRepository
import com.rabindradev.data.tables.UsersTable
import com.rabindradev.data.tables.insertUser
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class AuthRepositoryImpl(private val db: Database) : AuthRepository {

    override suspend fun getAllUsers(): List<User> = newSuspendedTransaction(db = db) {
        UsersTable.selectAll().map { it.toUser() }
    }

    override suspend fun getUserByEmail(email: String): User? = newSuspendedTransaction(db = db) {
        UsersTable.select { UsersTable.email eq email }.singleOrNull()?.toUser()
    }

    override suspend fun createUser(name: String, email: String): User = newSuspendedTransaction(db = db) {
        UsersTable.insertUser(name, email).getOrThrow()
        //insertUserRaw(name, email).getOrThrow()
    }

}