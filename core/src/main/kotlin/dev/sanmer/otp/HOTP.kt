package dev.sanmer.otp

import dev.sanmer.encoding.decodeBase32

object HOTP {
    enum class Hash {
        SHA1,
        SHA256,
        SHA512
    }

    private fun otp(
        hash: Hash,
        secret: ByteArray,
        counter: ByteArray,
        digits: Int
    ) = when (hash) {
        Hash.SHA1 -> counter.hmacSha1(secret)
        Hash.SHA256 -> counter.hmacSha256(secret)
        Hash.SHA512 -> counter.hmacSha512(secret)
    }.otp(digits)

    fun otp(
        hash: Hash,
        secret: ByteArray,
        counter: Long,
        digits: Int
    ) = otp(
        hash = hash,
        secret = secret,
        counter = counter.toByteArray(),
        digits = digits
    ).toString().padStart(digits, '0')

    fun otp(
        hash: Hash,
        secret: String,
        counter: Long,
        digits: Int
    ) = otp(
        hash = hash,
        secret = secret.decodeBase32(),
        counter = counter.toByteArray(),
        digits = digits
    ).toString().padStart(digits, '0')
}