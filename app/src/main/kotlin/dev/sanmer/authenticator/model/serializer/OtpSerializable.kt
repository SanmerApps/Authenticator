package dev.sanmer.authenticator.model.serializer

import dev.sanmer.authenticator.model.auth.Auth

interface OtpSerializable {
    val auth: Auth
}