package com.rabindradev.domain.serviceImpls

import com.rabindradev.data.repositories.AuthRepository
import com.rabindradev.domain.ResponseState
import com.rabindradev.domain.models.AdminResponse
import com.rabindradev.domain.models.GeneralResponse
import com.rabindradev.domain.services.AdminService

class AdminServiceImpl(private val authRepository: AuthRepository) : AdminService {

    override suspend fun generateAdminAccessCode(): ResponseState<GeneralResponse> {
        return runCatching {
            val accessCode = authRepository.generateAdminAccessCode()
            ResponseState.Success(GeneralResponse(true, accessCode))
        }.getOrElse {
            ResponseState.Error("Failed to generate access code: ${it.message}")
        }
    }

    override suspend fun validateAdminAccessCode(accessCode: String): ResponseState<GeneralResponse> {
        return runCatching {
            val isValid = authRepository.validateAdminAccessCode(accessCode)
            if (isValid) {
                ResponseState.Success(GeneralResponse(true, "Access code is valid"))
            } else {
                ResponseState.Error("Invalid access code")
            }
        }.getOrElse {
            ResponseState.Error("Failed to validate access code: ${it.message}")
        }
    }

    override suspend fun invalidateAdminAccessCode(accessCode: String): ResponseState<GeneralResponse> {
        return runCatching {
            authRepository.invalidateAdminAccessCode(accessCode)
            ResponseState.Success(GeneralResponse(true, "Access code invalidated"))
        }.getOrElse {
            ResponseState.Error("Failed to invalidate access code: ${it.message}")
        }
    }

    override suspend fun createAdmin(
        email: String, hashedPassword: String, name: String, phone: String?, token: String
    ): ResponseState<AdminResponse> {
        return runCatching {
            val admin = authRepository.createAdmin(email, hashedPassword, name, phone, token)
            if (admin != null) {
                ResponseState.Success(admin)
            } else {
                ResponseState.Error("Failed to create admin")
            }
        }.getOrElse {
            ResponseState.Error("Failed to create admin: ${it.message}")
        }
    }

    override suspend fun getAdminByEmail(email: String): ResponseState<AdminResponse?> {
        return runCatching {
            val admin = authRepository.getAdminByEmail(email)
            if (admin != null) {
                ResponseState.Success(admin)
            } else {
                ResponseState.Error("Admin not found")
            }
        }.getOrElse {
            ResponseState.Error("Failed to fetch admin: ${it.message}")
        }
    }

    override suspend fun getAllAdmins(): ResponseState<List<AdminResponse>> {
        return runCatching {
            val admins = authRepository.getAllAdmins()
            ResponseState.Success(admins)
        }.getOrElse {
            ResponseState.Error("Failed to fetch admins: ${it.message}")
        }
    }

    override suspend fun updateLastSeen(email: String): ResponseState<GeneralResponse> {
        return runCatching {
            authRepository.updateLastSeen(email)
            ResponseState.Success(GeneralResponse(true, "Last seen updated"))
        }.getOrElse {
            ResponseState.Error("Failed to update last seen: ${it.message}")
        }
    }

    override suspend fun deleteAdmin(email: String): ResponseState<GeneralResponse> {
        return runCatching {
            val isDeleted = authRepository.deleteAdmin(email)
            if (isDeleted) {
                ResponseState.Success(GeneralResponse(true, "Admin deleted successfully"))
            } else {
                ResponseState.Error("Admin not found")
            }
        }.getOrElse {
            ResponseState.Error("Failed to delete admin: ${it.message}")
        }
    }

    override suspend fun adminExists(email: String): ResponseState<GeneralResponse> {
        return runCatching {
            val exists = authRepository.adminExists(email)
            ResponseState.Success(GeneralResponse(exists, if (exists) "Admin exists" else "Admin does not exist"))
        }.getOrElse {
            ResponseState.Error("Failed to check if admin exists: ${it.message}")
        }
    }
}