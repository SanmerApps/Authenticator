package dev.sanmer.encoding

import io.matthewnelson.encoding.base64.Base64
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArray
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArrayOrNull
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToString

fun String.decodeBase64() = decodeToByteArray(Base64.Default)

fun String.isBase64() = decodeToByteArrayOrNull(Base64.Default) != null

fun ByteArray.encodeBase64() = encodeToString(Base64.Default)