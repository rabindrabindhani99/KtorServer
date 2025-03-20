package com.rabindradev.domain.serviceImpls

import com.rabindradev.data.models.UserDto
import com.rabindradev.data.repositories.AuthRepository
import com.rabindradev.domain.ResponseState
import com.rabindradev.domain.services.UserService
import org.mindrot.jbcrypt.BCrypt

class UserServiceImpl(private val authRepository: AuthRepository) : UserService {

    override suspend fun createUser(email: String, password: String): ResponseState<UserDto> {
        val hashedPassword = hashPassword(password)
        return runCatching {
            val user = authRepository.createUser(email, hashedPassword)
            ResponseState.Success(user)
        }.getOrElse {
            ResponseState.Error("Failed to create user: ${it.message}")
        }
    }

    override suspend fun authenticateUser(email: String, password: String): ResponseState<UserDto> {
        val user = authRepository.getUserByEmail(email) ?: return ResponseState.Error("User not found")
        return if (verifyPassword(password, user.password)) {
            ResponseState.Success(user)
        } else {
            ResponseState.Error("Invalid credentials")
        }
    }

    override suspend fun getAllUsers(): ResponseState<List<UserDto>> {
        return runCatching {
            ResponseState.Success(authRepository.getAllUsers())
        }.getOrElse {
            ResponseState.Error("Failed to fetch users: ${it.message}")
        }
    }

    private fun hashPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())

    private fun verifyPassword(password: String, hashedPassword: String): Boolean =
        BCrypt.checkpw(password, hashedPassword)
}