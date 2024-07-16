package dev.sanmer.otp

import dev.sanmer.encoding.decodeBase32
import kotlin.math.pow

object HOTP {
    enum class Hash {
        SHA1,
        SHA256,
        SHA512
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun ByteArray.otp(digits: Int): Int {
        val offset = this[size - 1].toInt() and 0x0F
        val code = ((this[offset].toInt() and 0x7F shl 24)
                or (this[offset + 1].toInt() and 0xFF shl 16)
                or (this[offset + 2].toInt() and 0xFF shl 8)
                or (this[offset + 3].toInt() and 0xFF))

        return code % 10.0.pow(digits).toInt()
    }

    private fun otp(hash: Hash, secret: ByteArray, counter: ByteArray, digits: Int) =
        when (hash) {
            Hash.SHA1 -> counter.hmacSha1(secret)
            Hash.SHA256 -> counter.hmacSha256(secret)
            Hash.SHA512 -> counter.hmacSha512(secret)
        }.otp(digits)

    fun otp(hash: Hash, secret: ByteArray, counter: Long, digits: Int) = otp(
        hash = hash,
        secret = secret,
        counter = counter.toByteArray(),
        digits = digits
    ).toString().padStart(digits, '0')

    fun otp(hash: Hash, secret: String, counter: Long, digits: Int) = otp(
        hash = hash,
        secret = secret.decodeBase32(),
        counter = counter.toByteArray(),
        digits = digits
    ).toString().padStart(digits, '0')
}