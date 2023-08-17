package com.nvvi9.ytstream.utils

import com.nvvi9.ytstream.model.streams.Stream
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
            .let { this - it.first.toSet() + it.second }
    }