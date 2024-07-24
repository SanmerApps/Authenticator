@file:Suppress("NOTHING_TO_INLINE")

package dev.sanmer.encoding

import io.matthewnelson.encoding.base32.Base32
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArrayOrNull
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToString
import io.matthewnelson.encoding.core.EncodingException
import java.nio.charset.Charset

inline fun String.isBase32(): Boolean {
    return isNotBlank() && (decodeToByteArrayOrNull(Base32.Default) != null
            || decodeToByteArrayOrNull(Base32.Hex) != null)
}

inline fun String.decodeBase32(): ByteArray {
    return decodeToByteArrayOrNull(Base32.Default)
        ?: decodeToByteArrayOrNull(Base32.Hex)
        ?: throw EncodingException(this)
}

inline fun String.decodeBase32(charset: Charset = Charsets.UTF_8) =
    decodeBase32().toString(charset)

inline fun ByteArray.encodeBase32Default() = encodeToString(Base32.Default)

inline fun ByteArray.encodeBase32Hex() = encodeToString(Base32.Hex)

inline fun String.encodeBase32Default(charset: Charset = Charsets.UTF_8) =
    toByteArray(charset).encodeBase32Default()

inline fun String.encodeBase32Hex(charset: Charset = Charsets.UTF_8) =
    toByteArray(charset).encodeBase32Hex()