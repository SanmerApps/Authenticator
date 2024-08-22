@file:Suppress("NOTHING_TO_INLINE")

package dev.sanmer.otp

import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

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

internal inline fun ByteArray.otp(digits: Int): Int {
    val offset = this[size - 1].toInt() and 0x0F
    val code = ((this[offset].toInt() and 0x7F shl 24)
            or (this[offset + 1].toInt() and 0xFF shl 16)
            or (this[offset + 2].toInt() and 0xFF shl 8)
            or (this[offset + 3].toInt() and 0xFF))

    return code % 10.0.pow(digits).toInt()
}