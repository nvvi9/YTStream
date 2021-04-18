package com.nvvi9.ytstream

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nvvi9.ytstream.model.PlaylistVideoData
import com.nvvi9.ytstream.model.PlaylistVideoDetails
import com.nvvi9.ytstream.model.VideoData
import com.nvvi9.ytstream.model.VideoDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith


@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class YTStreamTest {

    private val ytStream = YTStream()

    private val id =
        arrayOf(
            "UqLRqzTp6Rk",
            "u0BetD0OAcs",
            "uKM9ZuQB3MA",
            "1nX0kF2UwDc",
            "kfugSz3m_zA",
            "UqLRqzTp6Rk",
            "u0BetD0OAcs",
            "uKM9ZuQB3MA",
            "1nX0kF2UwDc",
            "kfugSz3m_zA"
        )

    private val playlistId = arrayOf(
        "PLqhFrLVOcKubTAHpven_G3ntVHQNwHILM",
        "PLp_L1ltSVItxcbJbKRyCd4KWZ4kHfDJ9U",
        "PLp_L1ltSVItxcg9D6ASZKzQpb5sMw8BAi",
        "PLp_L1ltSVItxFXp7iTvV-Yh-PhybfDD6j",
        "PLp_L1ltSVItzh2-0a05Y5LA4nLrdzaddl",
    )

    @Test
    fun videoDataExtraction() = runBlocking {
        ytStream.extractVideoData(*id)
            .toList()
            .let { videoData ->
                assertFalse("not full extraction", videoData.size != id.size)
                videoData.forEach { checkVideoData(it) }
            }
    }

    @Test
    fun videoDetailsExtraction() = runBlocking {
        ytStream.extractVideoDetails(*id)
            .toList()
            .let { videoDetails ->
                assertFalse("not full extraction", videoDetails.size != id.size)
                videoDetails.forEach { checkVideoDetails(it) }
            }
    }

    @Test
    fun videoDataExtractionRx() {
        ytStream.extractVideoDataObservable(*id)
            .toList()
            .blockingSubscribe { videoData ->
                assertFalse("not full extraction", videoData.size != id.size)
                videoData.forEach { checkVideoData(it) }
            }
    }

    @Test
    fun videoDetailsExtractionRx() {
        ytStream.extractVideoDetailsObservable(*id)
            .toList()
            .blockingSubscribe { videoDetails ->
                assertFalse("not full extraction", videoDetails.size != id.size)
                videoDetails.forEach { checkVideoDetails(it) }
            }
    }

    @Test
    fun playlistDataExtraction() = runBlocking {
        ytStream.extractPlaylistVideoData(*playlistId)
            .toList()
            .let { playlists ->
                assertFalse("not full extraction", playlists.size != playlistId.size)
                playlists.forEach { checkPlaylistVideoData(it) }
            }
    }

    @Test
    fun playlistDataExtractionRx() {
        ytStream.extractPlaylistVideoDataObservable(*playlistId)
            .toList()
            .blockingSubscribe { playlists ->
                assertFalse("not full extraction", playlists.size != playlistId.size)
                playlists.forEach { checkPlaylistVideoData(it) }
            }
    }

    @Test
    fun playlistVideoDetails() = runBlocking {
        ytStream.extractPlaylistVideoDetails(*playlistId)
            .toList()
            .let { playlists ->
                assertFalse("not full extraction", playlists.size != playlistId.size)
                playlists.forEach { checkPlaylistVideoDetails(it) }
            }
    }

    @Test
    fun playlistVideoDetailsRx() {
        ytStream.extractPlaylistVideoDetailsObservable(*playlistId)
            .toList()
            .blockingSubscribe { playlists ->
                assertFalse("not full extraction", playlists.size != playlistId.size)
                playlists.forEach { checkPlaylistVideoDetails(it) }
            }
    }

    private fun checkPlaylistVideoData(playlistVideoData: PlaylistVideoData) {
        playlistVideoData.run {
            assertFalse("empty playlistId", playlistId.isEmpty())
            assertFalse("empty playlist title", title.isEmpty())
            videoData.forEach {
                checkVideoData(it)
            }
        }
    }

    private fun checkPlaylistVideoDetails(playlistVideoDetails: PlaylistVideoDetails) {
        playlistVideoDetails.run {
            assertFalse("empty playlistId", playlistId.isEmpty())
            assertFalse("empty playlist title", title.isEmpty())
            videoDetails.forEach {
                checkVideoDetails(it)
            }
        }
    }

    private fun checkVideoData(videoData: VideoData?) {
        assertNotNull("null videoData", videoData)
        videoData?.run {
            checkVideoDetails(videoDetails)
            assertFalse("empty streams ${videoDetails.id}", streams.isEmpty())
        } ?: assertNotNull("null videoData", videoData)
    }

    private fun checkVideoDetails(videoDetails: VideoDetails?) {
        videoDetails?.run {
            Log.i(this::class.simpleName, "$id $thumbnails")
            assertNotNull("null id", id)
            assertNotNull("null channel $id", channel)
            assertNotNull("null title $id", title)
            assertNotNull("null expiresInSeconds $id", expiresInSeconds)
            assertFalse("empty thumbnails $id", thumbnails.isEmpty())
        } ?: assertNotNull("null videoDetails", videoDetails)
    }
}