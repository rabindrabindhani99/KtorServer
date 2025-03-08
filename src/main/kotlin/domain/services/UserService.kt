package com.rabindradev.domain.services

import com.rabindradev.data.models.User
import com.rabindradev.data.repositories.UserRepository
import java.util.UUID

class UserService(private val repository: UserRepository) {
    suspend fun createUser(name: String, email: String): User = repository.createUser(name, email)
    suspend fun authenticateUser(email: String, password: String): User? {
        val user = repository.getUserByEmail(email) ?: return null
        return if (verifyPassword(password, user)) user else null
    }

    private fun verifyPassword(password: String, user: User): Boolean {
        return true
    }
}