package dev.sanmer.authenticator.model.auth

import dev.sanmer.authenticator.Timer
import dev.sanmer.authenticator.ktx.updateDistinct
import dev.sanmer.encoding.decodeBase32
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri
import dev.sanmer.otp.TOTP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

data class TotpAuth(
    override val issuer: String,
    override val name: String,
    override val secret: String,
    val hash: HOTP.Hash,
    val digits: Int,
    val period: Long
) : Auth, Otp {
    private val secretBytes by lazy { secret.decodeBase32() }
    private val counter = MutableStateFlow(TOTP.epochSeconds / period)

    override val uri get() = OtpUri(
        type = Auth.Type.TOTP.name,
        name = name,
        issuer = issuer,
        secret = secret,
        algorithm = hash.name,
        digits = digits,
        period = period
    )

    override val progress = Timer.epochSeconds.map {
        counter.updateDistinct { it / period }
        (period - it % period) / period.toFloat()
    }

    override val otp = counter.map { new(it) }

    override fun now() = HOTP.otp(
        hash = hash,
        secret = secretBytes,
        digits = digits,
        counter = counter.value
    )

    override fun copy(secret: String) = copy(
        issuer = issuer,
        name = name,
        secret = secret
    )

    private fun new(counter: Long) = HOTP.otp(
        hash = hash,
        secret = secretBytes,
        digits = digits,
        counter = counter
    )
}
