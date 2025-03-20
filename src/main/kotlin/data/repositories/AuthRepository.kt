package com.rabindradev.data.repositories

import com.rabindradev.data.models.UserDto
import com.rabindradev.domain.models.AdminResponse

interface AuthRepository {
    // User-related functions
    suspend fun getAllUsers(): List<UserDto>
    suspend fun getUserByEmail(email: String): UserDto?
    suspend fun createUser(email: String, hashedPassword: String): UserDto

    // Admin Access Code functions
    suspend fun generateAdminAccessCode(expiryMinutes: Long = 60): String
    suspend fun validateAdminAccessCode(accessCode: String): Boolean
    suspend fun invalidateAdminAccessCode(accessCode: String)

    // Admin-related functions
    suspend fun createAdmin(
        email: String,
        hashedPassword: String,
        name: String,
        phone: String?,
        token: String
    ): AdminResponse?

    suspend fun getAdminByEmail(email: String): AdminResponse?
    suspend fun getAllAdmins(): List<AdminResponse>
    suspend fun updateLastSeen(email: String)
    suspend fun deleteAdmin(email: String): Boolean
    suspend fun adminExists(email: String): Boolean
}
