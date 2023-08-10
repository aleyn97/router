package com.aleyn.router.core

import android.os.Bundle

/**
 * @author: Aleyn
 * @date: 2023/7/17 16:20
 */
class RouterUrl internal constructor(
    internal val routerKey: String,
    private val queryNamesAndValues: List<String?>?,
    private val url: String
) {
    /**
     * 查询所有参数 转为 Bundle
     * @return Bundle
     */
    fun queryAllParameter(): Bundle? {
        if (queryNamesAndValues == null) return null
        val bundle = Bundle()
        for (i in queryNamesAndValues.indices step 2) {
            bundle.putString(queryNamesAndValues[i], queryNamesAndValues[i + 1])
        }
        return bundle
    }

    override fun equals(other: Any?): Boolean {
        return other is RouterUrl && other.url == url
    }

    override fun hashCode(): Int = url.hashCode()

    override fun toString(): String = url


    companion object {

        private fun parse(input: String): RouterUrl {
            val start = input.indexOfFirstNonAsciiWhitespace()
            val limit = input.indexOfLastNonAsciiWhitespace(start)

            val pos = input.indexOfFirst { it == '?' }
            return if (pos > -1 && pos < limit) {
                val routerKey = input.substring(start, pos)
                val param = input.substring(pos + 1)
                RouterUrl(routerKey, param.toQueryNamesAndValues(), input)
            } else {
                RouterUrl(input, null, input)
            }
        }

        private fun String.toQueryNamesAndValues(): MutableList<String?> {
            val result = mutableListOf<String?>()
            var pos = 0
            while (pos <= length) {
                var ampersandOffset = indexOf('&', pos)
                if (ampersandOffset == -1) ampersandOffset = length

                val equalsOffset = indexOf('=', pos)
                if (equalsOffset == -1 || equalsOffset > ampersandOffset) {
                    result.add(substring(pos, ampersandOffset))
                    result.add(null) // No value for this name.
                } else {
                    result.add(substring(pos, equalsOffset))
                    result.add(substring(equalsOffset + 1, ampersandOffset))
                }
                pos = ampersandOffset + 1
            }
            return result
        }

        internal fun String.toRouterUrl() = parse(this)
    }

}


fun String.indexOfFirstNonAsciiWhitespace(startIndex: Int = 0, endIndex: Int = length): Int {
    for (i in startIndex until endIndex) {
        when (this[i]) {
            '\t', '\n', '\u000C', '\r', ' ' -> Unit
            else -> return i
        }
    }
    return endIndex
}

fun String.indexOfLastNonAsciiWhitespace(startIndex: Int = 0, endIndex: Int = length): Int {
    for (i in endIndex - 1 downTo startIndex) {
        when (this[i]) {
            '\t', '\n', '\u000C', '\r', ' ' -> Unit
            else -> return i + 1
        }
    }
    return startIndex
}

internal fun String.getRouterKey(): String {
    val start = this.indexOfFirstNonAsciiWhitespace()
    val limit = this.indexOfLastNonAsciiWhitespace(start)
    val pos = this.indexOfFirst { it == '?' }
    return if (pos > -1 && pos < limit) this.substring(start, pos) else this
}