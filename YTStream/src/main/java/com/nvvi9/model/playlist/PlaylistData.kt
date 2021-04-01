package com.nvvi9.model.playlist

import com.nvvi9.extractors.VideoExtractor
import com.nvvi9.model.VideoData
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

@FlowPreview
data class PlaylistData(
    val playlistId: String,
    val title: String,
    val videoData: List<VideoData>
) {

    companion object {

        suspend fun fromPlaylistId(playlistId: String) = flow {
            RawPlaylist.fromPlaylistId(playlistId).getOrNull()?.let { rawPlaylist ->
                VideoExtractor
                    .extractVideoData(*rawPlaylist.videoContentId.toTypedArray())
                    .toList()
                    .let { emit(PlaylistData(rawPlaylist.playlistId, rawPlaylist.title, it)) }
            }
        }
    }
}