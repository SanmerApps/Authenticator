package dev.sanmer.authenticator.model.auth

import dev.sanmer.encoding.decodeBase32
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

data class HotpAuth(
    override val issuer: String,
    override val name: String,
    override val secret: String,
    val hash: HOTP.Hash,
    val digits: Int,
    private val counterFlow: MutableStateFlow<Long>
) : Auth, Otp {
    constructor(
        issuer: String,
        name: String,
        secret: String,
        hash: HOTP.Hash,
        digits: Int,
        counter: Long
    ) : this(
        issuer = issuer,
        name = name,
        secret = secret,
        hash = hash,
        digits = digits,
        counterFlow = MutableStateFlow(counter)
    )

    private val secretBytes by lazy { secret.decodeBase32() }

    val counter get() = counterFlow.value

    override val uri get() = OtpUri(
        type = Auth.Type.HOTP.name,
        name = name,
        issuer = issuer,
        secret = secret,
        algorithm = hash.name,
        digits = digits,
        counter = counter
    )

    override val progress = emptyFlow<Float>()

    override val otp = counterFlow.map { now() }

    override fun now() = HOTP.otp(
        hash = hash,
        secret = secretBytes,
        counter = counter,
        digits = digits
    )

    override fun copy(secret: String) = copy(
        issuer = issuer,
        name = name,
        secret = secret
    )

    fun new() { counterFlow.value += 1 }
}
