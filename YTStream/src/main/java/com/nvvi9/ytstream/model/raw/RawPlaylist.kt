package com.nvvi9.ytstream.model.raw

import com.nvvi9.ytstream.network.KtorService
import com.nvvi9.ytstream.utils.mapNotNull
import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern

@JvmInline
internal value class RawPlaylist private constructor(private val playlist: JSONObject) {

    private val contents: JSONArray get() = playlist.getJSONArray("contents")
    val playlistId: String get() = playlist.getString("playlistId")
    val title: String get() = playlist.getString("title")
    val videoContentId: List<String>
        get() = (0 until contents.length())
            .mapNotNull {
                contents.getJSONObject(it)
                    ?.getJSONObject("playlistPanelVideoRenderer")
                    ?.getString("videoId")
            }

    companion object {

        suspend fun fromPlaylistId(playlistId: String) =
            KtorService.getPlaylistPage(playlistId)
                .map { patternPlaylist.matcher(it) }
                .mapNotNull { matcher -> matcher.takeIf { it.find() }?.group(1) }
                .mapCatching { JSONObject(it) }
                .map { RawPlaylist(it) }


        private val patternPlaylist =
            Pattern.compile("\"playlist\":\\{\"playlist\":(.*?),\"autoplay\":\\{\"autoplay\"")
    }
}