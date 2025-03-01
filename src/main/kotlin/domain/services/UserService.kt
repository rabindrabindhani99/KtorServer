package com.rabindradev.domain.services

import com.rabindradev.data.models.User
import com.rabindradev.data.repositories.UserRepository
import java.util.UUID

class UserService(private val repository: UserRepository) {
    suspend fun getAllUsers(): List<User> = repository.getAllUsers()
    suspend fun getUserById(id: UUID): User? = repository.getUserById(id)
    suspend fun createUser(name: String, email: String): User = repository.createUser(name, email)
}