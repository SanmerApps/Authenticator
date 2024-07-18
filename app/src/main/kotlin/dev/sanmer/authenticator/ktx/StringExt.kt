package dev.sanmer.authenticator.ktx

import kotlin.math.min

fun String.hidden(limit: Int = 6, mask: Char = '\u2022'): String {
    val showLength = min(length / 2, limit)
    val hiddenLength = min(length - showLength, limit)
    val sb = StringBuilder()
    sb.appendRange(this, 0, showLength)
    repeat(hiddenLength) { sb.append(mask) }
    return sb.toString()
}