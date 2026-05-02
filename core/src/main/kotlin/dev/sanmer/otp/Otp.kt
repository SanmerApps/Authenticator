package dev.sanmer.otp

import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

object Otp {
    enum class Hash(internal val algorithm: String) {
        SHA1("HmacSHA1"),
        SHA256("HmacSHA256"),
        SHA512("HmacSHA512")
    }

    private fun Long.toBeBytes(): ByteArray =
        ByteBuffer.allocate(Long.SIZE_BYTES)
            .order(ByteOrder.BIG_ENDIAN)
            .putLong(this)
            .array()

    private fun otp(
        hash: Hash,
        secret: ByteArray,
        counter: ByteArray,
        digits: Int
    ): Int {
        val result = Mac.getInstance(hash.algorithm).let {
            it.init(SecretKeySpec(secret, hash.algorithm))
            it.doFinal(counter)
        }

        val offset = result.last().toInt() and 0x0F
        val binary = ((result[offset].toInt() and 0x7F shl 24)
                or (result[offset + 1].toInt() and 0xFF shl 16)
                or (result[offset + 2].toInt() and 0xFF shl 8)
                or (result[offset + 3].toInt() and 0xFF))

        return binary % 10.0.pow(digits).toInt()
    }

    fun otp(
        hash: Hash,
        secret: ByteArray,
        counter: Long,
        digits: Int
    ) = otp(
        hash = hash,
        secret = secret,
        counter = counter.toBeBytes(),
        digits = digits
    ).toString().padStart(digits, '0')
}