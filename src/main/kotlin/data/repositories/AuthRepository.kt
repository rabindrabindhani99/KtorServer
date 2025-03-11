package com.rabindradev.data.repositories

import com.rabindradev.data.models.User

interface AuthRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun getUserByEmail(email: String): User?
    suspend fun createUser(name: String, email: String): User
}
