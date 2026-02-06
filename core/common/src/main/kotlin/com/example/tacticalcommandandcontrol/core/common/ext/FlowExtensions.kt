package com.example.tacticalcommandandcontrol.core.common.ext

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen

sealed interface Result<out T> {
    data object Loading : Result<Nothing>
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable) : Result<Nothing>
}

fun <T> Flow<T>.asResult(): Flow<Result<T>> =
    map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it)) }

fun <T> Flow<T>.retryWithBackoff(
    maxAttempts: Long = 3,
    initialDelayMs: Long = 1000,
    maxDelayMs: Long = 30_000,
): Flow<T> = retryWhen { _, attempt ->
    if (attempt >= maxAttempts) return@retryWhen false
    val delayMs = (initialDelayMs * (1L shl attempt.toInt().coerceAtMost(20)))
        .coerceAtMost(maxDelayMs)
    delay(delayMs)
    true
}
