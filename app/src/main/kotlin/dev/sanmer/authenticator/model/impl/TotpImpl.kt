package dev.sanmer.authenticator.model.impl

import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.encoding.decodeBase32
import dev.sanmer.otp.HOTP
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

data class TotpImpl(
    val entity: TotpEntity,
    val epochSeconds: StateFlow<Long>
) {
    val secret by lazy { entity.secret.decodeBase32() }

    val otp = epochSeconds.map {
        HOTP.otp(
            hash = entity.hash,
            secret = secret,
            digits = entity.digits,
            counter = it / entity.period
        )
    }
}