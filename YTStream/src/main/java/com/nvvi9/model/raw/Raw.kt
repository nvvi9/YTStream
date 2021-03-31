package com.nvvi9.model.raw

import com.nvvi9.model.VideoDetails
import com.nvvi9.network.KtorService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow


internal class Raw(val videoPageSource: String, val videoDetails: VideoDetails) {

    companion object {

        suspend fun fromIdFlow(id: String) = flow {
            emit(fromId(id))
        }

        private suspend fun fromId(id: String) = coroutineScope {
            val videoPageSource = async {
                KtorService.getVideoPage(id).map { it.replace("\\\"", "\"") }
            }

            val videoDetails = async {
                VideoDetails.fromId(id)
            }

            videoPageSource.await().getOrNull()?.let { pageSource ->
                videoDetails.await().getOrNull()?.let { details ->
                    Raw(pageSource, details)
                }
            }
        }
    }
}