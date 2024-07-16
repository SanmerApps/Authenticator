package dev.sanmer.authenticator.model.auth

import dev.sanmer.otp.OtpUri
import kotlinx.coroutines.flow.Flow

interface Otp {
    val uri: OtpUri
    val progress: Flow<Float>
    val otp: Flow<String>
    fun now(): String
}