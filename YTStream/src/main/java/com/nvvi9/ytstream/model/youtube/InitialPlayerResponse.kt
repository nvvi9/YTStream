package com.nvvi9.ytstream.model.youtube

import kotlinx.serialization.Serializable

@Serializable
data class InitialPlayerResponse(
    val streamingData: StreamingData,
    val videoDetails: VideoDetails
) {

    @Serializable
    data class StreamingData(
        val expiresInSeconds: String,
        val formats: List<Format>,
        val adaptiveFormats: List<Format>
    ) {
        @Serializable
        data class Format(
            val itag: Int,
            val signatureCipher: String?,
            val type: String?,
            val url: String?
        )
    }

    @Serializable
    data class VideoDetails(
        val videoId: String,
        val title: String,
        val lengthSeconds: String,
        val channelId: String,
        val shortDescription: String,
        val thumbnail: Thumbnail,
        val viewCount: String,
        val author: String,
        val isLiveContent: Boolean
    ) {
        @Serializable
        data class Thumbnail(
            val thumbnails: List<Thumbnail>
        ) {
            @Serializable
            data class Thumbnail(
                val url: String,
                val width: Int,
                val height: Int
            )
        }
    }
}