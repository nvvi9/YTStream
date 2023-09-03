package com.nvvi9.ytstream.model

import com.nvvi9.ytstream.model.streams.Stream

data class VideoData(
    val videoDetails: VideoDetails,
    val streams: List<Stream>
)