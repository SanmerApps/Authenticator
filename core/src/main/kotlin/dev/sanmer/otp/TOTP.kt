package dev.sanmer.otp

object TOTP {
    val epochSeconds: Long
        inline get() = System.currentTimeMillis() / 1000

    fun otp(
        hash: HOTP.Hash,
        secret: ByteArray,
        digits: Int,
        period: Long,
        seconds: Long = epochSeconds
    ) = HOTP.otp(
        hash = hash,
        secret = secret,
        counter = seconds / period,
        digits = digits
    )

    fun otp(
        hash: HOTP.Hash,
        secret: String,
        digits: Int,
        period: Long,
        seconds: Long = epochSeconds
    ) = HOTP.otp(
        hash = hash,
        secret = secret,
        counter = seconds / period,
        digits = digits
    )
}