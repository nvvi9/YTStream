package com.nvvi9.extractors

import com.nvvi9.model.PlaylistVideoData
import com.nvvi9.model.PlaylistVideoDetails
import com.nvvi9.model.playlist.PlaylistData
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge

@FlowPreview
internal object PlaylistExtractor {

    fun extractPlaylistVideoData(vararg playlistId: String) =
        playlistId.asFlow()
            .flatMapMerge { PlaylistData.fromPlaylistId(it) }
            .flatMapMerge { PlaylistVideoData.fromPlaylistData(it) }

    fun extractPlaylistVideoDetails(vararg playlistId: String) =
        playlistId.asFlow()
            .flatMapMerge { PlaylistData.fromPlaylistId(it) }
            .flatMapMerge { PlaylistVideoDetails.fromPlaylistData(it) }
}