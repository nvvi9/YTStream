package com.nvvi9.ytstream.model.signature

import com.nvvi9.ytstream.model.streams.StreamDetails

data class EncodedSignature(
    val url: String,
    val signature: String,
    val streamDetails: StreamDetails
)