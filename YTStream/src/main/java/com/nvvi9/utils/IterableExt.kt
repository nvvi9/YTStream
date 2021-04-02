package com.nvvi9.utils

import java.util.*

fun <T, R> Iterable<T>.mapCatching(transform: (T) -> R): List<R> =
    ArrayList<R>().also { list ->
        forEach {
            runCatching { transform(it) }.getOrNull()
                ?.let { list.add(it) }
        }
    }