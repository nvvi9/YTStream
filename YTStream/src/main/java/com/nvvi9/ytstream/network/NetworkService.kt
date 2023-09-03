package com.nvvi9.ytstream.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


internal object NetworkService {

    private const val USER_ARGENT =
        "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.98 Safari/537.36"

    suspend fun getVideoPage(id: String): Result<String> =
        getRaw("https://www.youtube.com/watch?v=$id")

    suspend fun getJsFile(jsPath: String): Result<String> =
        getRaw("https://www.youtube.com$jsPath")

    private suspend fun getRaw(url: String) = withContext(Dispatchers.IO) {
        val urlConnection = URL(url).openConnection().apply {
            setRequestProperty("User-Agent", USER_ARGENT)
        } as HttpURLConnection

        runCatching {
            urlConnection.inputStream.bufferedReader().readText()
        }.also { urlConnection.disconnect() }
    }
}