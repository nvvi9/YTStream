package com.nvvi9.ytstream.model

import com.nvvi9.ytstream.js.JsExecutor
import com.nvvi9.ytstream.model.streams.EncodedStreams
import com.nvvi9.ytstream.model.streams.Stream
import com.nvvi9.ytstream.utils.encode
import kotlinx.coroutines.flow.flow


data class VideoData(
    val videoDetails: VideoDetails,
    val streams: List<Stream>
) {

    companion object {

        internal suspend fun fromEncodedStreamsFlow(encodedStreams: EncodedStreams?) = flow {
            emit(encodedStreams?.run {
                jsCode?.let { script ->
                    JsExecutor.executeScript(script).getOrNull()?.split("\n")?.let {
                        VideoData(
                            videoDetails,
                            streams.encode(it, encodedSignatures)
                        )
                    }
                } ?: VideoData(videoDetails, streams)
            })
        }
    }
}