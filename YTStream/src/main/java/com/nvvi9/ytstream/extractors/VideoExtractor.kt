package com.nvvi9.ytstream.extractors

import com.nvvi9.ytstream.model.VideoData
import com.nvvi9.ytstream.model.VideoDetails
import com.nvvi9.ytstream.model.raw.Raw
import com.nvvi9.ytstream.model.streams.EncodedStreams
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapMerge

@FlowPreview
internal object VideoExtractor {

    fun extractVideoData(vararg id: String) =
        id.asFlow()
            .flatMapMerge { Raw.fromIdFlow(it) }
            .flatMapMerge { EncodedStreams.fromRawFlow(it) }
            .flatMapMerge { VideoData.fromEncodedStreamsFlow(it) }
            .filterNotNull()

    fun extractVideoDetails(vararg id: String) =
        id.asFlow()
            .flatMapMerge { VideoDetails.fromIdFlow(it) }
            .filterNotNull()
}