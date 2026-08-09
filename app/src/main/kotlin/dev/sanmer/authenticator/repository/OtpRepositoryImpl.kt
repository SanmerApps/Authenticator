package dev.sanmer.authenticator.repository

import dev.sanmer.auth.Otp
import dev.sanmer.auth.decodeBase32
import dev.sanmer.authenticator.model.otp.Totp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OtpRepositoryImpl(
    private val timeRepository: TimeRepository
) : OtpRepository {
    override suspend fun totp(totp: Totp) = withContext(Dispatchers.IO) {
        val secret = totp.secret.decodeBase32()
        val now = Otp.otp(
            hash = totp.hash,
            secret = secret,
            counter = timeRepository.now().epochSeconds / totp.period,
            digits = totp.digits
        )
        val otp = timeRepository.now
            .map { it.epochSeconds / totp.period }
            .distinctUntilChanged()
            .map {
                Otp.otp(
                    hash = totp.hash,
                    secret = secret,
                    counter = it,
                    digits = totp.digits
                )
            }
        now to otp
    }
}