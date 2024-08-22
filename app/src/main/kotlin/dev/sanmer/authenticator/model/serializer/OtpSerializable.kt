package dev.sanmer.authenticator.model.serializer

import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.otp.OtpUri

interface OtpSerializable {
    val auth: Auth
    val uri: OtpUri
}