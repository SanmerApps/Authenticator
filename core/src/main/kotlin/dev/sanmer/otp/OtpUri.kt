package dev.sanmer.otp

import android.net.Uri

class OtpUri private constructor() {
    constructor(
        issuer: String,
        name: String,
        type: String,
        secret: String,
        algorithm: String?,
        digits: Int?,
        counter: Long? = null,
        period: Long? = null
    ) : this() {
        this.issuer = issuer
        this.name = name
        this.type = type
        this.secret = secret
        this.algorithm = algorithm
        this.digits = digits
        this.counter = counter
        this.period = period
    }

    var issuer: String = ""
        private set

    var name: String = ""
        private set

    var type: String = ""
        private set

    var secret: String = ""
        private set

    var algorithm: String? = null
        private set

    var digits: Int? = null
        private set

    var counter: Long? = null
        private set

    var period: Long? = null
        private set

    private fun fromUri(uri: Uri): OtpUri {
        require(uri.isOtpUri()) { "Expected scheme = $SCHEME" }
        type = requireNotNull(uri.host) { "Unknown type" }.uppercase()
        secret = requireNotNull(uri.getQueryParameter(Query.SECRET)) { "Unknown secret" }.uppercase()
        algorithm = uri.getQueryParameter(Query.ALGORITHM)?.uppercase()
        digits = uri.getQueryParameter(Query.DIGITS)?.let(String::toInt)
        counter = uri.getQueryParameter(Query.COUNTER)?.let(String::toLong)
        period = uri.getQueryParameter(Query.PERIOD)?.let(String::toLong)

        val label = uri.path?.substring(1) ?: ""
        if (label.contains(":")) {
            val values = label.split(":".toRegex(), limit = 2)
            if (values.size >= 2) {
                issuer = values[0]
                name = values[1]
            } else {
                issuer = ""
                name = label
            }
        } else {
            issuer = uri.getQueryParameter(Query.ISSUER) ?: ""
            name = label
        }

        return this
    }

    private fun toUri(): Uri {
        val builder = Uri.Builder()
        builder.scheme(SCHEME)
        builder.authority(type.lowercase())
        builder.appendQueryParameter(Query.SECRET, secret)
        algorithm?.let { builder.appendQueryParameter(Query.ALGORITHM, it) }
        digits?.let { builder.appendQueryParameter(Query.DIGITS, it.toString()) }
        period?.let { builder.appendQueryParameter(Query.PERIOD, it.toString()) }
        counter?.let { builder.appendQueryParameter(Query.COUNTER, it.toString()) }

        if (issuer.isNotBlank()) {
            builder.appendQueryParameter(Query.ISSUER, issuer)
            if (name.isNotBlank()) {
                builder.path("${issuer}:${name}")
            } else {
                builder.path(issuer)
            }
        } else {
            if (name.isNotBlank()) {
                builder.path(name)
            }
        }

        return builder.build()
    }

    override fun toString(): String {
        return toUri().toString()
    }

    internal object Query {
        const val SECRET = "secret"
        const val ALGORITHM = "algorithm"
        const val DIGITS = "digits"
        const val PERIOD = "period"
        const val COUNTER = "counter"
        const val ISSUER = "issuer"
    }

    companion object Default {
        const val SCHEME = "otpauth"

        fun parse(uriString: String) = Uri.parse(uriString).let(OtpUri()::fromUri)

        fun String.toOtpUri() = parse(this)

        fun String.isOtpUri() = startsWith(SCHEME)

        fun Uri.isOtpUri() = scheme == SCHEME
    }
}