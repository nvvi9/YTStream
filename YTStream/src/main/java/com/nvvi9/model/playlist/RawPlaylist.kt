package com.nvvi9.model.playlist

import com.nvvi9.network.KtorService
import com.nvvi9.utils.mapNotNull
import org.json.JSONObject
import java.util.regex.Pattern

internal inline class RawPlaylist private constructor(private val playlist: JSONObject) {

    private val contents get() = playlist.getJSONArray("contents")
    val playlistId get() = playlist.getString("playlistId")
    val title get() = playlist.getString("title")
    val videoContentId
        get() = (0 until contents.length())
            .mapNotNull {
                contents.getJSONObject(it)
                    ?.getJSONObject("playlistPanelVideoRenderer")
                    ?.getString("videoId")
            }

    companion object {

        internal suspend fun fromPlaylistId(playlistId: String) =
            KtorService.getPlaylistPage(playlistId)
                .map { patternPlaylist.matcher(it) }
                .mapNotNull { matcher -> matcher.takeIf { it.find() }?.group(1) }
                .mapCatching { JSONObject(it) }
                .map { RawPlaylist(it) }


        private val patternPlaylist =
            Pattern.compile("\"playlist\":\\{\"playlist\":(.*?),\"autoplay\":\\{\"autoplay\"")
    }
}