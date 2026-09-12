package dev.sanmer.auth

import io.matthewnelson.encoding.base32.Base32
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArray
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToString

fun String.decodeBase32() = decodeToByteArray(Base32.Default)
fun ByteArray.encodeBase32() = encodeToString(Base32.Default)