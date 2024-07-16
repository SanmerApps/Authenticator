@file:Suppress("NOTHING_TO_INLINE")

package dev.sanmer.encoding

import io.matthewnelson.encoding.base32.Base32
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArrayOrNull
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToString
import io.matthewnelson.encoding.core.EncodingException

inline fun String.decodeBase32(): ByteArray {
    return decodeToByteArrayOrNull(Base32.Default)
        ?: decodeToByteArrayOrNull(Base32.Hex)
        ?: throw EncodingException(this)
}

inline fun String.isBase32(): Boolean {
    return decodeToByteArrayOrNull(Base32.Default) != null
            || decodeToByteArrayOrNull(Base32.Hex) != null
}

fun ByteArray.encodeBase32Default() = encodeToString(Base32.Default)

fun ByteArray.encodeBase32Hex() = encodeToString(Base32.Hex)