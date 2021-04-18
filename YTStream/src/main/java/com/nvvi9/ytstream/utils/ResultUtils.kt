package com.nvvi9.ytstream.utils

internal fun <T> T?.takeIfNotNull() =
    this?.let { Result.success(it) } ?: Result.failure(Exception("extension applied on null value"))

internal inline fun <R> runCatchingNull(block: () -> R?) =
    block()?.let {
        Result.success(it)
    } ?: Result.failure(Exception("returning null value"))

internal inline fun <T> T.getIf(predicate: (T) -> Boolean) =
    if (predicate(this)) Result.success(this) else Result.failure(Exception("not matching predicate"))

internal inline fun <T, R> Result<T>.mapNotNull(transform: (value: T) -> R?) =
    runCatchingNull { getOrNull()?.let { transform(it) } }