package com.nvvi9.ytstream.extractors

import android.content.Context
import com.nvvi9.ytstream.js.JsExecutor
import com.nvvi9.ytstream.model.signature.EncodedSignature
import com.nvvi9.ytstream.model.streams.Stream
import com.nvvi9.ytstream.model.youtube.InitialPlayerResponse
import com.nvvi9.ytstream.network.NetworkService
import com.nvvi9.ytstream.utils.decodeUrl
import java.util.regex.Pattern

class StreamExtractor(private val context: Context) {

    suspend fun extractStreams(
        pageHtml: String,
        formats: List<InitialPlayerResponse.StreamingData.Format>
    ): List<Stream> = extractUnencodedStreams(formats) + extractEncodedStreams(pageHtml, formats)

    private fun extractUnencodedStreams(formats: List<InitialPlayerResponse.StreamingData.Format>) =
        formats.mapNotNull { format ->
            val itag = format.itag
            format.url?.replace("\\u0026", "&")?.let { url ->
                Stream.ITAG_MAP[itag]?.let { streamDetails ->
                    Stream(url, streamDetails)
                }
            }
        }

    private suspend fun extractEncodedStreams(
        pageHtml: String,
        formats: List<InitialPlayerResponse.StreamingData.Format>
    ): List<Stream> = getEncodedSignatures(formats)
        .takeIf { it.isNotEmpty() }
        ?.let { encodedSignatures ->
            val matcher = patternDecryptionJsFile.matcher(pageHtml).takeIf { it.find() }
                ?: patternDecryptionJsFileWithoutSlash.matcher(pageHtml)

            val decipherJsFileName =
                matcher.takeIf { it.find() }?.group(0)?.replace("\\/", "/")
                    ?: return emptyList()

            val signatures = decipherSignature(encodedSignatures, decipherJsFileName)
                .map { it.split("\n") }
                .getOrDefault(emptyList())

            encodedSignatures.zip(signatures).map { (encSignature, sig) ->
                Stream("${encSignature.url}&sig=$sig", encSignature.streamDetails)
            }
        } ?: emptyList()

    private fun getEncodedSignatures(formats: List<InitialPlayerResponse.StreamingData.Format>) =
        formats.mapNotNull { format ->
            format.signatureCipher?.let { signatureCipher ->
                val matcherSigEncUrl = patternSigEncUrl.matcher(signatureCipher)
                    .takeIf { it.find() }
                    ?: return@mapNotNull null
                val matcherSignature = patternSignature.matcher(signatureCipher)
                    .takeIf { it.find() }
                    ?: return@mapNotNull null

                val url = matcherSigEncUrl.group(1)?.decodeUrl() ?: return@mapNotNull null
                val signature = matcherSignature.group(1)?.decodeUrl() ?: return@mapNotNull null
                val itag = format.itag

                Stream.ITAG_MAP[itag]?.let { EncodedSignature(url, signature, it) }
            }
        }

    private suspend fun decipherSignature(
        encSignatures: List<EncodedSignature>,
        decipherJsFileName: String
    ): Result<String> {
        val javaScriptFile =
            NetworkService.getJsFile(decipherJsFileName)
                .onFailure {
                    println(it.stackTraceToString())
                }.getOrNull() ?: throw IllegalStateException()

        val matcher =
            patternSignatureDecFunction.matcher(javaScriptFile).takeIf { it.find() }
                ?: throw IllegalStateException()
        val decipherFunctionName = matcher.group(1) ?: throw IllegalStateException()
        val patternMainVariable = Pattern.compile(
            "(var |\\s|,|;)"
                    + decipherFunctionName.replace("$", "\\$")
                    + "(=function\\((.{1,3})\\)\\{)"
        )

        var mainDecipherFunct: String

        var mainDecipherFunctionMatcher = patternMainVariable.matcher(javaScriptFile)
        if (mainDecipherFunctionMatcher.find()) {
            mainDecipherFunct =
                "var $decipherFunctionName${mainDecipherFunctionMatcher.group(2)}"
        } else {
            val patternMainFunction = Pattern.compile(
                "function "
                        + decipherFunctionName.replace("$", "\\$")
                        + "(\\((.{1,3})\\)\\{)"
            )

            mainDecipherFunctionMatcher = patternMainFunction.matcher(javaScriptFile)

            if (!mainDecipherFunctionMatcher.find()) throw IllegalStateException()

            mainDecipherFunct =
                "function ${decipherFunctionName}${mainDecipherFunctionMatcher.group(2)}"
        }

        var startIndex = mainDecipherFunctionMatcher.end()

        var i = startIndex
        var braces = 1

        while (i < javaScriptFile.length) {
            if (braces == 0 && startIndex + 5 < i) {
                mainDecipherFunct += "${javaScriptFile.substring(startIndex, i)};"
                break
            }
            if (javaScriptFile[i] == '{') {
                braces++
            } else if (javaScriptFile[i] == '}') {
                braces--
            }
            i++
        }

        var decipherFunctions = mainDecipherFunct

        val variableFunctionMatcher = patternVariableFunction.matcher(mainDecipherFunct)
        while (variableFunctionMatcher.find()) {
            val variableDef = "var ${variableFunctionMatcher.group(2)}={"
            if (variableDef in decipherFunctions) {
                continue
            }

            startIndex = javaScriptFile.indexOf(variableDef) + variableDef.length
            i = startIndex
            braces = 1
            while (i < javaScriptFile.length) {
                if (braces == 0) {
                    decipherFunctions += "$variableDef${
                        javaScriptFile.substring(
                            startIndex,
                            i
                        )
                    };"
                    break
                }
                if (javaScriptFile[i] == '{') {
                    braces++
                } else if (javaScriptFile[i] == '}') {
                    braces--
                }
                i++
            }
        }

        val functionMatcher = patternFunction.matcher(mainDecipherFunct)
        while (functionMatcher.find()) {
            val functionDef = "function ${functionMatcher.group(2)}("
            if (functionDef in decipherFunctions) {
                continue
            }

            startIndex = javaScriptFile.indexOf(functionDef) + functionDef.length
            i = 0
            braces = 0
            while (i < javaScriptFile.length) {
                if (braces == 0 && startIndex + 5 < i) {
                    decipherFunctions += "$functionDef${
                        javaScriptFile.substring(startIndex, i)
                    };"
                    break
                }
                if (javaScriptFile[i] == '{') {
                    braces++
                } else if (javaScriptFile[i] == '}') {
                    braces--
                }
                i++
            }
        }

        return decipherEncodedSignatures(
            encSignatures.map { it.signature },
            decipherFunctions,
            decipherFunctionName
        ).onFailure {
            println(it.stackTraceToString())
        }
    }

    private suspend fun decipherEncodedSignatures(
        encSignatures: List<String>,
        decipherFunctions: String,
        decipherFunctionName: String
    ): Result<String> {
        val script =
            "$decipherFunctions function decipher(){return " + encSignatures.foldIndexed("") { index, acc, s ->
                acc + decipherFunctionName + "('" + s + if (index < encSignatures.size - 1) "')+\"\\n\"+" else "')"
            } + "};decipher();"

        return JsExecutor.executeScript(context, script).onFailure {
            println(it.stackTraceToString())
        }
    }

    companion object {

        private val patternSigEncUrl = Pattern.compile("url=(.+?)(\\u0026|$)")
        private val patternSignature = Pattern.compile("s=(.+?)(\\u0026|$)")
        private val patternDecryptionJsFile =
            Pattern.compile("\\\\/s\\\\/player\\\\/([^\"]+?)\\.js")
        private val patternDecryptionJsFileWithoutSlash = Pattern.compile("/s/player/([^\"]+?).js")
        private val patternSignatureDecFunction =
            Pattern.compile("(?:\\b|[^a-zA-Z0-9$])([a-zA-Z0-9$]{1,4})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)")
        private val patternVariableFunction =
            Pattern.compile("([{; =])([a-zA-Z$][a-zA-Z0-9$]{0,2})\\.([a-zA-Z$][a-zA-Z0-9$]{0,2})\\(")
        private val patternFunction = Pattern.compile("([{; =])([a-zA-Z\$_][a-zA-Z0-9$]{0,2})\\(")
    }
}