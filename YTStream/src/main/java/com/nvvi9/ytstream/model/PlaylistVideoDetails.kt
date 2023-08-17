package com.nvvi9.ytstream.model

import com.nvvi9.ytstream.extractors.VideoExtractor
import com.nvvi9.ytstream.model.playlist.PlaylistData
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

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