package com.nvvi9.ytstream.model

import com.nvvi9.ytstream.model.raw.RawResponse
import com.nvvi9.ytstream.utils.mapNotNull
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow


data class VideoDetails(
    val id: String,
    val title: String,
    val channel: String?,
    val channelId: String?,
    val description: String?,
    val durationSeconds: Long?,
    val viewCount: Long?,
    val thumbnails: List<Thumbnail>,
    val expiresInSeconds: Long?,
    val isLiveStream: Boolean?,
    internal val isSignatureEncoded: Boolean,
    internal val statusOk: Boolean,
    internal val rawResponse: RawResponse
) {

    companion object {

        internal suspend fun fromId(id: String) = coroutineScope {
            val thumbnailUrl = "https://img.youtube.com/vi/$id"
            val thumbnails = listOf(
                Thumbnail(120, 90, "$thumbnailUrl/default.jpg"),
                Thumbnail(320, 180, "$thumbnailUrl/mqdefault.jpg"),
                Thumbnail(480, 360, "$thumbnailUrl/hqdefault.jpg")
            )

            RawResponse.fromId(id)
                .mapNotNull { rawResponse ->
                    rawResponse.id?.let { id ->
                        rawResponse.title?.let { title ->
                            VideoDetails(
                                id, title,
                                rawResponse.author, rawResponse.channelId, rawResponse.description,
                                rawResponse.durationSeconds, rawResponse.viewCount,
                                rawResponse.thumbnails ?: thumbnails,
                                rawResponse.expiresInSeconds, rawResponse.isLiveStream,
                                rawResponse.isEncoded, rawResponse.statusOk, rawResponse
                            )
                        }
                    }
                }
        }

        internal suspend fun fromIdFlow(id: String) = flow {
            emit(fromId(id).getOrNull())
        }
    }
}
