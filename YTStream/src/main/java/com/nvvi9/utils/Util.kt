package com.nvvi9.utils

import com.nvvi9.model.streams.Stream
import java.net.URLDecoder
import java.net.URLEncoder


internal fun String.decode(): String =
    URLDecoder.decode(this, "UTF-8")

internal fun String.encode(): String =
    URLEncoder.encode(this, "UTF-8")

internal fun List<Stream>.encode(decodeSignatures: List<String>, encSignatures: Map<Int, String>) =
    encSignatures.keys.zip(decodeSignatures).toMap().let { signatures ->
        filter { signatures.any { (key, _) -> it.streamDetails.itag == key } }
            .map { it to it.copy(url = it.url.plus("&sig=${signatures[it.streamDetails.itag]}")) }
            .unzip()
            .let { this - it.first + it.second }
    }

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