package com.rabindradev.domain.services

import com.rabindradev.domain.ResponseState
import com.rabindradev.domain.models.AdminResponse
import com.rabindradev.domain.models.GeneralResponse

interface AdminService {
    suspend fun generateAdminAccessCode(): ResponseState<GeneralResponse>
    suspend fun validateAdminAccessCode(accessCode: String): ResponseState<GeneralResponse>
    suspend fun invalidateAdminAccessCode(accessCode: String): ResponseState<GeneralResponse>
    suspend fun createAdmin(
        email: String, hashedPassword: String, name: String, phone: String?, token: String
    ): ResponseState<AdminResponse>

    suspend fun getAdminByEmail(email: String): ResponseState<AdminResponse?>
    suspend fun getAllAdmins(): ResponseState<List<AdminResponse>>
    suspend fun updateLastSeen(email: String): ResponseState<GeneralResponse>
    suspend fun deleteAdmin(email: String): ResponseState<GeneralResponse>
    suspend fun adminExists(email: String): ResponseState<GeneralResponse>
}