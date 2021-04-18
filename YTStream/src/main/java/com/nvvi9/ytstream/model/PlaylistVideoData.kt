package com.nvvi9.ytstream.model

import com.nvvi9.ytstream.extractors.VideoExtractor
import com.nvvi9.ytstream.model.playlist.PlaylistData
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

@FlowPreview
data class PlaylistVideoData(
    val playlistId: String,
    val title: String,
    val videoData: List<VideoData>
) {

    companion object {

        internal suspend fun fromPlaylistData(playlistData: PlaylistData) = flow {
            VideoExtractor
                .extractVideoData(*playlistData.videoId.toTypedArray())
                .toList()
                .let { emit(PlaylistVideoData(playlistData.playlistId, playlistData.title, it)) }
        }
    }
}