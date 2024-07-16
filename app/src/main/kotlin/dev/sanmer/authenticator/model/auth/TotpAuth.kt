package dev.sanmer.authenticator.model.auth

import dev.sanmer.authenticator.ktx.updateDistinct
import dev.sanmer.encoding.decodeBase32
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri
import dev.sanmer.otp.TOTP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.seconds

data class TotpAuth(
    override val issuer: String,
    override val name: String,
    override val secret: String,
    val hash: HOTP.Hash,
    val digits: Int,
    val period: Long
) : Auth, Otp {
    private val secretBytes by lazy { secret.decodeBase32() }
    private val counter = MutableStateFlow(count)

    override val uri get() = OtpUri(
        type = Auth.Type.TOTP.name,
        name = name,
        issuer = issuer,
        secret = secret,
        algorithm = hash.name,
        digits = digits,
        period = period
    )

    override val progress = flow {
        while (currentCoroutineContext().isActive) {
            val elapsedSeconds = TOTP.epochSeconds % period
            val lifetime = period - elapsedSeconds

            emit(lifetime / period.toFloat())
            counter.updateDistinct { count }

            delay(1.seconds)
        }
    }.flowOn(Dispatchers.Default)

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

    private val count: Long
        inline get() = TOTP.epochSeconds / period

    private fun new(counter: Long) = HOTP.otp(
        hash = hash,
        secret = secretBytes,
        digits = digits,
        counter = counter
    )
}
