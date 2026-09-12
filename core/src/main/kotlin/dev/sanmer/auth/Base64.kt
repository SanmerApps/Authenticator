package dev.sanmer.auth

import io.matthewnelson.encoding.base64.Base64
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArray
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToString

fun String.decodeBase64() = decodeToByteArray(Base64.Default)
fun ByteArray.encodeBase64() = encodeToString(Base64.Default)