package com.nvvi9.js

import com.squareup.duktape.Duktape
import kotlinx.coroutines.coroutineScope


internal object JsExecutor {

    private val duktape: Duktape = Duktape.create()

    suspend fun executeScript(script: String) = coroutineScope {
        runCatching {
            duktape.evaluate(script) as String?
        }
    }
}