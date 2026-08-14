package dev.sanmer.authenticator.repository

import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.database.model.AuthProperties
import dev.sanmer.authenticator.model.otp.Totp
import kotlinx.coroutines.flow.Flow

interface OtpRepository {
    suspend fun otp(auth: AuthProperties) = when (auth.auth.type) {
        Auth.Type.TOTP -> totp(Totp(auth))
    }

    suspend fun totp(totp: Totp): Pair<String, Flow<String>>
}