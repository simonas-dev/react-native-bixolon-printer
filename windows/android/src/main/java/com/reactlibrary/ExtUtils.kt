package com.reactlibrary

fun <T> Result.Companion.failure(message: String): Result<T> {
    return Result.failure<T>(Throwable(message))
}