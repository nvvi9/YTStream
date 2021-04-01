package com.nvvi9

import com.nvvi9.extractors.PlaylistExtractor
import com.nvvi9.extractors.VideoExtractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.rx3.asObservable


@ExperimentalCoroutinesApi
@FlowPreview
class YTStream {

    fun extractVideoData(vararg id: String) =
        VideoExtractor.extractVideoData(*id)

    fun extractVideoDetails(vararg id: String) =
        VideoExtractor.extractVideoDetails(*id)

    fun extractPlaylistData(vararg playlistId: String) =
        PlaylistExtractor.extractPlaylistVideoData(*playlistId)

    fun extractVideoDataObservable(vararg id: String) =
        extractVideoData(*id).asObservable()

    fun extractVideoDetailsObservable(vararg id: String) =
        extractVideoDetails(*id).asObservable()

    fun extractPlaylistDataObservable(vararg playlistId: String) =
        extractPlaylistData(*playlistId).asObservable()
}