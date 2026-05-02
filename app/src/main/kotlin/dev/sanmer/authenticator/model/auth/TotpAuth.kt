package dev.sanmer.authenticator.model.auth

import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.encoding.decodeBase32
import dev.sanmer.otp.Otp
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

data class TotpAuth(
    val entity: TotpEntity,
    val epochSeconds: StateFlow<Long>
) {
    val secret by lazy { entity.secret.decodeBase32() }

    val otp = epochSeconds.map {
        Otp.otp(
            hash = entity.hash,
            secret = secret,
            digits = entity.digits,
            counter = it / entity.period
        )
    }

    fun otp() = Otp.otp(
        hash = entity.hash,
        secret = secret,
        digits = entity.digits,
        counter = epochSeconds.value / entity.period
    )
}