package dev.sanmer.otp

import android.net.Uri

class OtpUri private constructor() {
    constructor(
        issuer: String,
        name: String,
        type: String,
        secret: String,
        algorithm: String,
        digits: Int,
        counter: Long? = null,
        period: Long? = null
    ) : this() {
        this.issuer = issuer
        this.name = name
        this.type = type.lowercase()
        this.secret = secret
        this.algorithm = algorithm.uppercase()
        this.digits = digits
        this.counter = counter
        this.period = period
    }

    constructor(uri: Uri) : this() {
        val label = uri.getOrDefault("") { path?.substring(1) }
        if (label.contains(":")) {
            val strings = label.split(":".toRegex(), limit = 2)
            if (strings.size == 2) {
                issuer = strings.first()
                name = strings.last()
            } else {
                issuer = ""
                name = label
            }
        } else {
            issuer = uri.getQueryParameterOrDefault("issuer", "")
            name = label
        }

        type = uri.getOrDefault("") { host }
        secret = uri.getQueryParameterOrDefault("secret", "")
        algorithm = uri.getQueryParameterOrDefault("algorithm", "")
        digits = uri.getQueryParameterOrDefault("digits", "0").let(String::toInt)

        counter = uri.getQueryParameter("counter")?.let(String::toLong)
        period = uri.getQueryParameter("period")?.let(String::toLong)
    }

    var issuer: String = ""
        private set

    var name: String = ""
        private set

    var type: String = ""
        private set

    var secret: String = ""
        private set

    var algorithm: String = ""
        private set

    var digits: Int = 0
        private set

    var counter: Long? = null
        private set

    var period: Long? = null
        private set

    fun toUri(): Uri {
        val builder = Uri.Builder()
        builder.scheme(SCHEME)
        builder.authority(type)

        if (issuer.isNotEmpty()) {
            builder.appendQueryParameter("issuer", issuer)
            if (name.isNotEmpty()) {
                builder.path("${issuer}:${name}")
            } else {
                builder.path(issuer)
            }
        } else {
            builder.path(name)
        }

        builder.appendQueryParameter("secret", secret)
        builder.appendQueryParameter("algorithm", algorithm)
        builder.appendQueryParameter("digits", digits.toString())

        period?.let { builder.appendQueryParameter("period", it.toString()) }
        counter?.let { builder.appendQueryParameter("counter", it.toString()) }

        return builder.build()
    }

    override fun toString(): String {
        return toUri().toString()
    }

    companion object {
        const val SCHEME = "otpauth"

        fun parse(uriString: String): OtpUri {
            val uri = Uri.parse(uriString)
            if (!uri.isOtpAuthUri()) {
                throw IllegalArgumentException("Unsupported ${uri.scheme}")
            }

            return OtpUri(uri)
        }

        fun String.isOtpAuthUri() = startsWith(SCHEME)

        fun Uri.isOtpAuthUri() = scheme == SCHEME

        internal inline fun <T> Uri.getOrDefault(default: T, block: Uri.() -> T?) =
            block(this) ?: default

        @Suppress("NOTHING_TO_INLINE")
        internal inline fun Uri.getQueryParameterOrDefault(key: String, default: String) =
            getQueryParameter(key) ?: default
    }
}