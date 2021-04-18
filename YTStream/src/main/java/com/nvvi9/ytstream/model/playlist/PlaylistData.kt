package com.nvvi9.ytstream.model.playlist

import com.nvvi9.ytstream.model.raw.RawPlaylist
import kotlinx.coroutines.flow.flow

internal data class PlaylistData(
    val playlistId: String,
    val title: String,
    val videoId: List<String>
) {

    companion object {

        suspend fun fromPlaylistId(playlistId: String) = flow {
            RawPlaylist.fromPlaylistId(playlistId).getOrNull()?.let {
                emit(PlaylistData(it.playlistId, it.title, it.videoContentId))
            }
        }
    }
}
