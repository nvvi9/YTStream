package com.nvvi9.ytstream

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nvvi9.ytstream.model.PlaylistVideoData
import com.nvvi9.ytstream.model.PlaylistVideoDetails
import com.nvvi9.ytstream.model.VideoData
import com.nvvi9.ytstream.model.VideoDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
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

    private val playlistId = arrayOf("PLqhFrLVOcKubTAHpven_G3ntVHQNwHILM")

    @Test
    fun videoDataExtraction() = runBlocking {
        ytStream.extractVideoData(*id).collect {
            checkVideoData(it)
        }
    }

    @Test
    fun videoDetailsExtraction() = runBlocking {
        ytStream.extractVideoDetails(*id).collect {
            checkVideoDetails(it)
        }
    }

    @Test
    fun videoDataExtractionRx() {
        ytStream.extractVideoDataObservable(*id).blockingSubscribe {
            checkVideoData(it)
        }
    }

    @Test
    fun videoDetailsExtractionRx() {
        ytStream.extractVideoDetailsObservable(*id).blockingSubscribe {
            checkVideoDetails(it)
        }
    }

    @Test
    fun playlistDataExtraction() = runBlocking {
        ytStream.extractPlaylistVideoData(*playlistId)
            .collect { checkPlaylistVideoData(it) }

    }

    @Test
    fun playlistDataExtractionRx() {
        ytStream.extractPlaylistVideoDataObservable(*playlistId)
            .blockingSubscribe { checkPlaylistVideoData(it) }
    }

    @Test
    fun playlistVideoDetails() = runBlocking {
        ytStream.extractPlaylistVideoDetails(*playlistId)
            .collect { checkPlaylistVideoDetails(it) }
    }

    @Test
    fun playlistVideoDetailsRx() {
        ytStream.extractPlaylistVideoDetailsObservable(*playlistId)
            .blockingSubscribe { checkPlaylistVideoDetails(it) }
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