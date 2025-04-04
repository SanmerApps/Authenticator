package dev.sanmer.authenticator.model.auth

import dev.sanmer.encoding.decodeBase32
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri
import dev.sanmer.otp.TOTP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

data class TotpAuth(
    override val issuer: String,
    override val name: String,
    override val secret: String,
    val hash: HOTP.Hash,
    val digits: Int,
    val period: Long,
    private val epochSecondsFlow: StateFlow<Long> = MutableStateFlow(TOTP.epochSeconds)
) : Auth, Otp {
    private val secretBytes by lazy { secret.decodeBase32() }

    override val uri get() = OtpUri(
        type = Auth.Type.TOTP.name,
        name = name,
        issuer = issuer,
        secret = secret,
        algorithm = hash.name,
        digits = digits,
        period = period
    )

    override val progress = epochSecondsFlow.map { (period - it % period) / period.toFloat() }

    override val otp = epochSecondsFlow.map { new(it / period) }

    override fun now() = HOTP.otp(
        hash = hash,
        secret = secretBytes,
        digits = digits,
        counter = epochSecondsFlow.value / period
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
