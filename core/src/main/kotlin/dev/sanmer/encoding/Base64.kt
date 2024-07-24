@file:Suppress("NOTHING_TO_INLINE")

package dev.sanmer.encoding

import io.matthewnelson.encoding.base64.Base64
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArray
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArrayOrNull
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToString
import java.nio.charset.Charset

inline fun String.isBase64() = isNotBlank() && decodeToByteArrayOrNull(Base64.Default) != null

inline fun String.decodeBase64() = decodeToByteArray(Base64.Default)

inline fun String.decodeBase64(charset: Charset = Charsets.UTF_8) =
    decodeToByteArray(Base64.Default).toString(charset)

inline fun ByteArray.encodeBase64() = encodeToString(Base64.Default)

inline fun String.encodeBase64(charset: Charset = Charsets.UTF_8) =
    toByteArray(charset).encodeBase64()