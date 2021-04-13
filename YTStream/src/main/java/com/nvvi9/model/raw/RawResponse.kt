package com.nvvi9.model.raw

import com.nvvi9.model.Thumbnail
import com.nvvi9.network.KtorService
import com.nvvi9.utils.decode
import com.nvvi9.utils.getIf
import kotlinx.coroutines.coroutineScope
import org.json.JSONArray
import java.util.regex.Pattern


@Suppress("BlockingMethodInNonBlockingContext")
inline class RawResponse private constructor(val raw: String) {

    val id get() = patternVideoId.matcher(raw).takeIf { it.find() }?.group(1)
    val title get() = patternTitle.matcher(raw).takeIf { it.find() }?.group(1)
    val isLiveStream get() = patternHlsvp.matcher(raw).find()
    val author get() = patternAuthor.matcher(raw).takeIf { it.find() }?.group(1)
    val channelId get() = patternChannelId.matcher(raw).takeIf { it.find() }?.group(1)
    val description get() = patternShortDescription.matcher(raw).takeIf { it.find() }?.group(1)
    val durationSeconds
        get() = patternLengthSeconds.matcher(raw).takeIf { it.find() }?.group(1)?.toLong()
    val viewCount get() = patternViewCount.matcher(raw).takeIf { it.find() }?.group(1)?.toLong()
    val expiresInSeconds
        get() = patternExpiresInSeconds.matcher(raw).takeIf { it.find() }?.group(1)?.toLong()
    val isEncoded get() = patternCipher.matcher(raw).find()
    val statusOk get() = patternStatusOk.matcher(raw).find()
    val thumbnails
        get() = patternThumbnails.matcher(raw)
            .getIf { it.find() }
            .map { matcher ->
                (1..matcher.groupCount())
                    .fold(listOf<String?>()) { thumbnails, group -> thumbnails + matcher.group(group) }
                    .filterNotNull()
            }.map { thumbnailsJson ->
                thumbnailsJson.filter { thumbnail -> id?.let { thumbnail.contains(it) } ?: false }
            }.mapCatching { thumbnailsJson ->
                thumbnailsJson
                    .map { JSONArray("[$it]") }
            }.mapCatching { jsonArrays ->
                jsonArrays.flatMap { jsonArray ->
                    (0 until jsonArray.length()).map {
                        jsonArray.getJSONObject(it).run {
                            Thumbnail(getInt("width"), getInt("height"), getString("url"))
                        }
                    }
                }
            }.onFailure {
                it.printStackTrace()
            }.getOrNull()

    companion object {

        internal suspend fun fromId(id: String) = coroutineScope {
            KtorService.getVideoInfo(id)
                .mapCatching { it.decode().replace("\\u0026", "&") }
                .map { RawResponse(it) }
        }

        private val patternTitle =
            Pattern.compile("\"title\"\\s*:\\s*\"(.*?)\"")
        private val patternVideoId =
            Pattern.compile("\"videoId\"\\s*:\\s*\"(.+?)\"")
        private val patternAuthor =
            Pattern.compile("\"author\"\\s*:\\s*\"(.+?)\"")
        private val patternChannelId =
            Pattern.compile("\"channelId\"\\s*:\\s*\"(.+?)\"")
        private val patternLengthSeconds =
            Pattern.compile("\"lengthSeconds\"\\s*:\\s*\"(\\d+?)\"")
        private val patternViewCount =
            Pattern.compile("\"viewCount\"\\s*:\\s*\"(\\d+?)\"")
        private val patternExpiresInSeconds =
            Pattern.compile("\"expiresInSeconds\"\\s*:\\s*\"(\\d+?)\"")
        private val patternShortDescription =
            Pattern.compile("\"shortDescription\"\\s*:\\s*\"(.+?)\"")
        private val patternStatusOk =
            Pattern.compile("status=ok(&|,|\\z)")
        private val patternHlsvp =
            Pattern.compile("hlsvp=(.+?)(&|\\z)")
        private val patternCipher =
            Pattern.compile("\"signatureCipher\"\\s*:\\s*\"(.+?)\"")
        private val patternThumbnails =
            Pattern.compile("\"thumbnails\":\\[(.*?)\\]\\}")
    }
}