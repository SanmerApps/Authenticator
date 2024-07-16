package dev.sanmer.authenticator.ktx

fun String.toLongOr(default: Long) =
    try {
        toLong()
    } catch (_: NumberFormatException) {
        default
    }

fun String.toIntOr(default: Int) =
    try {
        toInt()
    } catch (_: NumberFormatException) {
        default
    }