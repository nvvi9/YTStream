package com.nvvi9.model

import com.nvvi9.extractors.VideoExtractor
import com.nvvi9.model.playlist.PlaylistData
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

@FlowPreview
data class PlaylistVideoDetails(
    val playlistId: String,
    val title: String,
    val videoDetails: List<VideoDetails>
) {

    companion object {

       internal suspend fun fromPlaylistData(playlistData: PlaylistData) = flow {
            VideoExtractor
                .extractVideoDetails(*playlistData.videoId.toTypedArray())
                .toList()
                .let { emit(PlaylistVideoDetails(playlistData.playlistId, playlistData.title, it)) }
        }
    }
}