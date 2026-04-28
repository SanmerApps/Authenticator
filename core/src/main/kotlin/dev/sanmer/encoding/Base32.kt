@file:Suppress("NOTHING_TO_INLINE")

package dev.sanmer.encoding

import io.matthewnelson.encoding.base32.Base32
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArray
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArrayOrNull
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToString
import java.nio.charset.Charset

inline fun String.isBase32() = isNotBlank() && decodeToByteArrayOrNull(Base32.Default) != null

inline fun String.decodeBase32() = decodeToByteArray(Base32.Default)

inline fun String.decodeBase32(charset: Charset = Charsets.UTF_8) =
    decodeBase32().toString(charset)

inline fun ByteArray.encodeBase32() = encodeToString(Base32.Default)

inline fun String.encodeBase32(charset: Charset = Charsets.UTF_8) =
    toByteArray(charset).encodeBase32()