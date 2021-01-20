package com.nvvi9.ytstream

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nvvi9.YTStream
import com.nvvi9.model.VideoData
import com.nvvi9.model.VideoDetails
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

    private fun checkVideoData(videoData: VideoData?) {
        assertNotNull("null videoData", videoData)
        videoData?.run {
            checkVideoDetails(videoDetails)
            assertFalse("empty streams ${videoDetails.id}", streams.isEmpty())
        } ?: assertNotNull("null videoData", videoData)
    }

    private fun checkVideoDetails(videoDetails: VideoDetails?) {
        videoDetails?.run {
            assertNotNull("null id", id)
            assertNotNull("null channel $id", channel)
            assertNotNull("null title $id", title)
            assertNotNull("null expiresInSeconds $id", expiresInSeconds)
        } ?: assertNotNull("null videoDetails", videoDetails)
    }
}