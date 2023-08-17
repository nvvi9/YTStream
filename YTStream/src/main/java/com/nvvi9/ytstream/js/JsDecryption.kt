package com.nvvi9.ytstream.js

import com.nvvi9.ytstream.network.KtorService
import com.nvvi9.ytstream.utils.mapNotNull
import com.nvvi9.ytstream.utils.takeIfNotNull
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern


internal data class JsDecryption(
    val mainVariable: String,
    val decryptionFunction: String
) {

    companion object {

        private val jsHashMap = ConcurrentHashMap<String, JsDecryption>()

        suspend fun fromVideoPageSource(videPageSource: String) = coroutineScope {
            (patternDecryptionJsFile.matcher(videPageSource).takeIf { it.find() }
                ?: patternDecryptionJsFileWithoutSlash.matcher(videPageSource).takeIf { it.find() })
                .takeIfNotNull()
                .mapNotNull { it.group(0)?.replace("\\/", "/") }
                .mapNotNull { jsPath ->
                    jsHashMap[jsPath] ?: KtorService.getJsFile(jsPath).getOrNull()
                        ?.replace("\n", " ")
                        ?.let { jsFile ->
                            patternSignatureDecryptionFunction.matcher(jsFile).takeIf { it.find() }
                                ?.let { matcher ->
                                    val decryptionFunction = matcher.group(1)
                                    val patternMainVariable =
                                        Pattern.compile(
                                            "(var |\\s|,|;)${
                                                decryptionFunction?.replace("$", "\\$")
                                            }(=function\\((.{1,3})\\)\\{)"
                                        )
                                    var jsFileMatcher = patternMainVariable.matcher(jsFile)
                                    var mainVariable: String = if (jsFileMatcher.find()) {
                                        "var $decryptionFunction ${jsFileMatcher.group(2)}"
                                    } else {
                                        val patternMainFunction =
                                            Pattern.compile(
                                                "function ${
                                                    decryptionFunction?.replace("$", "\\$")
                                                }(\\((.{1,3})\\)\\{)"
                                            )
                                        jsFileMatcher = patternMainFunction.matcher(jsFile)
                                        "function ${jsFileMatcher.takeIf { it.find() }?.group(2)}"
                                    }
                                    var startIndex = jsFileMatcher.end()
                                    var braces = 1
                                    for (i in startIndex until jsFile.length) {
                                        if (braces == 0 && startIndex + 5 < i) {
                                            mainVariable += jsFile.substring(
                                                startIndex,
                                                i
                                            ) + ";"
                                            break
                                        }
                                        if (jsFile[i] == '{') {
                                            braces++
                                        } else if (jsFile[i] == '}') {
                                            braces--
                                        }
                                    }

                                    jsFileMatcher = patternVariableFunction.matcher(mainVariable)

                                    while (jsFileMatcher.find()) {
                                        val variableDef = "var ${jsFileMatcher.group(2)}={"
                                        if (mainVariable.contains(variableDef)) {
                                            continue
                                        }
                                        startIndex =
                                            jsFile.indexOf(variableDef) + variableDef.length

                                        braces = 1

                                        for (i in startIndex until jsFile.length) {
                                            if (braces == 0) {
                                                mainVariable += variableDef + jsFile.substring(
                                                    startIndex,
                                                    i
                                                ) + ";"
                                                break
                                            }
                                            if (jsFile[i] == '{') {
                                                braces++
                                            } else if (jsFile[i] == '}') {
                                                braces--
                                            }
                                        }
                                    }

                                    jsFileMatcher = patternFunction.matcher(mainVariable)
                                    while (jsFileMatcher.find()) {
                                        val functionDef = "function ${jsFileMatcher.group(2)}("
                                        if (mainVariable.contains(functionDef)) {
                                            continue
                                        }
                                        startIndex =
                                            jsFile.indexOf(functionDef) + functionDef.length

                                        braces = 0

                                        for (i in startIndex until jsFile.length) {
                                            if (braces == 0 && startIndex + 5 < i) {
                                                mainVariable += functionDef + jsFile.substring(
                                                    startIndex,
                                                    i
                                                ) + ";"
                                                break
                                            }
                                            if (jsFile[i] == '{') {
                                                braces++
                                            } else if (jsFile[i] == '}') {
                                                braces--
                                            }
                                        }
                                    }
                                    decryptionFunction?.let { function ->
                                        JsDecryption(mainVariable, function)
                                            .also { it -> jsHashMap[jsPath] = it }
                                    }
                                }
                        }
                }
        }

        private val patternVariableFunction: Pattern =
            Pattern.compile("([{; =])([a-zA-Z$][a-zA-Z0-9$]{0,2})\\.([a-zA-Z$][a-zA-Z0-9$]{0,2})\\(")
        private val patternFunction: Pattern =
            Pattern.compile("([{; =])([a-zA-Z\$_][a-zA-Z0-9$]{0,2})\\(")
        private val patternDecryptionJsFile: Pattern =
            Pattern.compile("\\\\/player\\\\/([^\"]+?)\\.js")
        private val patternSignatureDecryptionFunction: Pattern =
            Pattern.compile("(?:\\b|[^a-zA-Z0-9\$])([a-zA-Z0-9\$]{2})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)")
        private val patternDecryptionJsFileWithoutSlash: Pattern =
            Pattern.compile("/s/player/([^\"]+?).js")
    }
}