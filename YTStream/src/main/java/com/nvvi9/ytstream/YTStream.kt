package com.nvvi9.ytstream

import android.content.Context
import com.nvvi9.ytstream.extractors.StreamExtractor
import com.nvvi9.ytstream.model.VideoData
import com.nvvi9.ytstream.model.VideoDetails
import com.nvvi9.ytstream.model.toVideoDetails
import com.nvvi9.ytstream.model.youtube.InitialPlayerResponse
import com.nvvi9.ytstream.network.NetworkService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.util.regex.Pattern


class YTStream(context: Context) {

    private val streamExtractor = StreamExtractor(context)

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json(builderAction = {
        explicitNulls = false
        ignoreUnknownKeys = true
    })

    suspend fun extractVideoData(id: String): Result<VideoData> =
        NetworkService.getVideoPage(id).mapCatching { pageHtml ->
            val playerResponse =
                patternPlayerResponse.matcher(pageHtml).takeIf { it.find() }?.group(1)
                    ?: throw IllegalStateException()

            val ytPlayerResponse = json.decodeFromString<InitialPlayerResponse>(playerResponse)
            val streamingData = ytPlayerResponse.streamingData

            val videoDetails = ytPlayerResponse.toVideoDetails()

            val formats = (streamingData.formats + streamingData.adaptiveFormats)
                .filter { it.type != "FORMAT_STREAM_TYPE_OTF" }

            val streams = streamExtractor.extractStreams(pageHtml, formats)
            VideoData(videoDetails, streams)
        }

    suspend fun extractVideoDetails(id: String): Result<VideoDetails> =
        NetworkService.getVideoPage(id).mapCatching { pageHtml ->
            val playerResponse =
                patternPlayerResponse.matcher(pageHtml).takeIf { it.find() }?.group(1)
                    ?: throw IllegalStateException()

            val ytPlayerResponse = json.decodeFromString<InitialPlayerResponse>(playerResponse)
            ytPlayerResponse.toVideoDetails()
        }

    companion object {

        private val patternPlayerResponse =
            Pattern.compile("var ytInitialPlayerResponse\\s*=\\s*(\\{.+?\\})\\s*;")
    }
}
