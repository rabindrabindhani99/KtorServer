package com.rabindradev.data.repositoriesImpl

import com.rabindradev.data.models.UserDto
import com.rabindradev.data.models.toUser
import com.rabindradev.data.repositories.AuthRepository
import com.rabindradev.data.tables.*
import com.rabindradev.domain.models.AdminResponse
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class AuthRepositoryImpl(private val db: Database) : AuthRepository {

    override suspend fun getAllUsers(): List<UserDto> = newSuspendedTransaction(db = db) {
        UsersTable.selectAll().map { it.toUser() }
    }

    override suspend fun getUserByEmail(email: String): UserDto? = newSuspendedTransaction(db = db) {
        UsersTable.select { UsersTable.email eq email }.mapNotNull { it.toUser() }.singleOrNull()
    }

    override suspend fun createUser(email: String, hashedPassword: String): UserDto = newSuspendedTransaction(db = db) {
        UsersTable.insertUser(email, hashedPassword).getOrThrow()
    }

    override suspend fun generateAdminAccessCode(expiryMinutes: Long): String {
        return AdminAccessCodesTable.insertAccessCode()
    }

    override suspend fun validateAdminAccessCode(accessCode: String): Boolean {
        return AdminAccessCodesTable.validateAccessCode(accessCode)
    }

    override suspend fun invalidateAdminAccessCode(accessCode: String) {
        AdminAccessCodesTable.invalidateAccessCode(accessCode)
    }

    override suspend fun createAdmin(
        email: String, hashedPassword: String, name: String, phone: String?, token: String
    ): AdminResponse? {
        return AdminsTable.insertAdmin(email, hashedPassword, name, phone, token)
    }

    override suspend fun getAdminByEmail(email: String): AdminResponse? {
        return AdminsTable.getAdminByEmail(email)
    }

    override suspend fun getAllAdmins(): List<AdminResponse> {
        return AdminsTable.getAllAdmins()
    }

    override suspend fun updateLastSeen(email: String) {
        return AdminsTable.updateLastSeen(email)
    }

    override suspend fun deleteAdmin(email: String): Boolean {
        return AdminsTable.deleteAdmin(email)
    }

    override suspend fun adminExists(email: String): Boolean {
        return AdminsTable.adminExists(email)
    }
}