package dev.sanmer.auth

import android.net.Uri

data class OtpUri(
    val type: String,
    val name: String,
    val issuer: String,
    val secret: String,
    val algorithm: String?,
    val digits: Int?,
    val period: Long?
) {
    fun toUri(): Uri {
        val builder = Uri.Builder()
        builder.scheme(SCHEME)
        builder.authority(type)
        builder.appendQueryParameter(Query.SECRET, secret)
        algorithm?.let { builder.appendQueryParameter(Query.ALGORITHM, it) }
        digits?.let { builder.appendQueryParameter(Query.DIGITS, it.toString()) }
        period?.let { builder.appendQueryParameter(Query.PERIOD, it.toString()) }
        if (issuer.isNotEmpty()) {
            builder.appendQueryParameter(Query.ISSUER, issuer)
            builder.path(if (name.isNotEmpty()) "${issuer}:${name}" else issuer)
        } else if (name.isNotEmpty()) {
            builder.path(name)
        }
        return builder.build()
    }

    private object Query {
        const val SECRET = "secret"
        const val ALGORITHM = "algorithm"
        const val DIGITS = "digits"
        const val PERIOD = "period"
        const val ISSUER = "issuer"
    }

    companion object Default {
        const val SCHEME = "otpauth"

        fun Uri.isOtpUri() = scheme == SCHEME

        fun Uri.toOtpUri(): OtpUri {
            require(isOtpUri()) { "Expect scheme = $SCHEME" }
            val label = path?.substring(1).orEmpty()
            val (issuer, name) = if (label.contains(":")) {
                val values = label.split(":".toRegex(), limit = 2)
                values[0] to values[1]
            } else {
                getQueryParameter(Query.ISSUER).orEmpty() to label
            }
            return OtpUri(
                type = requireNotNull(host) { "Expect type" },
                name = name,
                issuer = issuer,
                secret = requireNotNull(getQueryParameter(Query.SECRET)) { "Expect secret" },
                algorithm = getQueryParameter(Query.ALGORITHM)?.uppercase(),
                digits = getQueryParameter(Query.DIGITS)?.let(String::toInt),
                period = getQueryParameter(Query.PERIOD)?.let(String::toLong)
            )
        }
    }
}