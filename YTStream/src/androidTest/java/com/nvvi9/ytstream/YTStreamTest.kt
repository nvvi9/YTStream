package com.nvvi9.ytstream

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nvvi9.ytstream.model.VideoData
import com.nvvi9.ytstream.model.VideoDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class YTStreamTest {

    private val ytStream = YTStream(InstrumentationRegistry.getInstrumentation().context)

    private val id =
        arrayOf(
            "dz6YbMtj74U",
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
        id.map { ytStream.extractVideoData(it) }
            .forEach { videoDataResult ->
                assertTrue("result not succeed", videoDataResult.isSuccess)
                checkVideoData(videoDataResult.getOrNull())
            }
    }

    @Test
    fun videoDetailsExtraction() = runBlocking {
        id.map { ytStream.extractVideoDetails(it) }
            .forEach { videoDetailsResult->
                assertTrue("result not succeed", videoDetailsResult.isSuccess)
                checkVideoDetails(videoDetailsResult.getOrNull())
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
            assertFalse("empty thumbnails $id", thumbnails.isEmpty())
        } ?: assertNotNull("null videoDetails", videoDetails)
    }
}