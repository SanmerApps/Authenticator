package dev.sanmer.authenticator.model.auth

interface Auth {
    val issuer: String
    val name: String
    val secret: String
    fun copy(secret: String): Auth

    val displayName
        get() = if (name.isEmpty()) {
            issuer
        } else {
            "$issuer (${name})"
        }

    enum class Type {
        HOTP,
        TOTP
    }
}