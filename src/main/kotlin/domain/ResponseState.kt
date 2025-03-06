package com.rabindradev.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual

@Serializable
sealed class ResponseState<out T> {
    @Serializable
    data class Success<out T>(val data: T) : ResponseState<T>()

    @Serializable
    data class Error(val message: String, @Contextual val throwable: Throwable? = null) : ResponseState<Nothing>()

    @Serializable
    object Loading : ResponseState<Nothing>()

    @Serializable
    object Idle : ResponseState<Nothing>()
}

fun <T> ResponseState<T>.isLoading(): Boolean = this is ResponseState.Loading
fun <T> ResponseState<T>.isSuccess(): Boolean = this is ResponseState.Success
fun <T> ResponseState<T>.isError(): Boolean = this is ResponseState.Error
fun <T> ResponseState<T>.isIdle(): Boolean = this is ResponseState.Idle