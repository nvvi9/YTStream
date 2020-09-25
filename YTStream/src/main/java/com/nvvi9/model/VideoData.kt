package com.nvvi9.model

import com.nvvi9.js.JsExecutor
import com.nvvi9.model.streams.EncodedStreams
import com.nvvi9.model.streams.Stream
import com.nvvi9.utils.encodeStreams
import kotlinx.coroutines.flow.flow


data class VideoData(
    val videoDetails: VideoDetails,
    val streams: List<Stream>
) {

    companion object {

        internal suspend fun fromEncodedStreamsFlow(encodedStreams: EncodedStreams?) = flow {
            emit(encodedStreams?.run {
                jsCode?.let { script ->
                    JsExecutor.executeScript(script)?.split("\n")?.let {
                        VideoData(
                            videoDetails,
                            streams.toMutableList().encodeStreams(it, encodedSignatures)
                        )
                    }
                } ?: VideoData(videoDetails, streams)
            })
        }
    }
}