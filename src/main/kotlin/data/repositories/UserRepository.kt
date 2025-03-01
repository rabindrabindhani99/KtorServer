package com.rabindradev.data.repositories

import com.rabindradev.data.models.User
import java.util.UUID

interface UserRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun getUserById(id: UUID): User?
    suspend fun createUser(name: String, email: String): User
}
