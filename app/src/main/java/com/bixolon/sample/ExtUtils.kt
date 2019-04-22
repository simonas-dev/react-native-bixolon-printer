package com.bixolon.sample

fun <T> Result.Companion.failure(message: String): Result<T> {
    return Result.failure<T>(Throwable(message))
}