package dev.sanmer.authenticator.model

import android.net.Uri
import dev.sanmer.auth.Otp
import kotlinx.serialization.Serializable

@Serializable
data class OtpUri(
    val type: Type,
    val name: String,
    val secret: String,
    val issuer: String = "",
    val hash: Otp.Hash = Otp.Hash.SHA1,
    val digits: Int = 6,
    val period: Long = 30
) {
    enum class Type {
        TOTP
    }

    fun toUri(): Uri {
        val builder = Uri.Builder()
        builder.scheme(SCHEME)
        builder.authority(type.name.lowercase())
        builder.appendQueryParameter("secret", secret)
        builder.appendQueryParameter("algorithm", hash.name)
        builder.appendQueryParameter("digits", digits.toString())
        builder.appendQueryParameter("period", period.toString())
        if (issuer.isNotEmpty()) {
            builder.appendQueryParameter("issuer", issuer)
            builder.path("${issuer}:${name}")
        } else {
            builder.path(name)
        }
        return builder.build()
    }

    companion object Default {
        const val SCHEME = "otpauth"

        fun Uri.isOtpUri() = scheme == SCHEME

        fun Uri.toOtpUri(): OtpUri {
            require(isOtpUri()) { "Expect scheme = $SCHEME" }
            val host = requireNotNull(host) { "Expect host" }
            val secret = requireNotNull(getQueryParameter("secret")) { "Expect secret" }
            val type = Type.valueOf(host.uppercase())
            val name: String
            val issuer: String
            val label = lastPathSegment.orEmpty()
            if (label.contains(":")) {
                val values = label.split(":", limit = 2)
                issuer = values[0].ifEmpty { getQueryParameter("issuer").orEmpty() }
                name = values[1]
            } else {
                issuer = getQueryParameter("issuer").orEmpty()
                name = label
            }
            val hash = getQueryParameter("algorithm")?.let { Otp.Hash.valueOf(it.uppercase()) }
            val digits = getQueryParameter("digits")?.let(String::toInt)
            val period = getQueryParameter("period")?.let(String::toLong)
            return OtpUri(
                type = type,
                name = name,
                issuer = issuer,
                secret = secret,
                hash = hash ?: Otp.Hash.SHA1,
                digits = digits ?: 6,
                period = period ?: 30
            )
        }
    }
}