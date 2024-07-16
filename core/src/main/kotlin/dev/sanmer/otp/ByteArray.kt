@file:Suppress("NOTHING_TO_INLINE")

package dev.sanmer.otp

import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

internal inline fun Long.toByteArray() =
    ByteBuffer.allocate(Long.SIZE_BYTES)
        .order(ByteOrder.BIG_ENDIAN)
        .putLong(this)
        .array()

internal inline fun ByteArray.hmac(key: ByteArray, algorithm: String): ByteArray =
    Mac.getInstance(algorithm).let {
        it.init(SecretKeySpec(key, algorithm))
        it.doFinal(this)
    }

internal inline fun ByteArray.hmacSha1(key: ByteArray) = hmac(key, "HmacSHA1")

internal inline fun ByteArray.hmacSha256(key: ByteArray) = hmac(key, "HmacSHA256")

internal inline fun ByteArray.hmacSha512(key: ByteArray) = hmac(key, "HmacSHA512")