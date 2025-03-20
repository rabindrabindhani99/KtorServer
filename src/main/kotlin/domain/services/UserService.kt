package com.rabindradev.domain.services

import com.rabindradev.data.models.UserDto
import com.rabindradev.domain.ResponseState

interface UserService {
    suspend fun createUser(email: String, password: String): ResponseState<UserDto>
    suspend fun authenticateUser(email: String, password: String): ResponseState<UserDto>
    suspend fun getAllUsers(): ResponseState<List<UserDto>>
}