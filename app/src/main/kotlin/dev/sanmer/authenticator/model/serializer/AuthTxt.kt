package dev.sanmer.authenticator.model.serializer

import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.HotpAuth
import dev.sanmer.authenticator.model.auth.TotpAuth
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri.Default.toOtpUri
import java.io.InputStream
import java.io.OutputStream

data class AuthTxt(
    val hotp: List<HotpSerializable> = emptyList(),
    val totp: List<TotpSerializable> = emptyList()
) : AuthSerializable<OtpSerializable> {
    constructor(
        auths: List<Auth>
    ) : this(
        hotp = auths.filterIsInstance<HotpAuth>().map(::HotpSerializable),
        totp = auths.filterIsInstance<TotpAuth>().map(::TotpSerializable)
    )

    override val auths: List<OtpSerializable>
        get() = hotp.toMutableList<OtpSerializable>()
            .apply { addAll(totp) }
            .toList()

    override fun encodeTo(output: OutputStream) {
        val uris = auths.map { it.uri.toString() }
        val content = uris.joinToString(separator = "\n")
        output.write(content.toByteArray())
    }

    companion object Default {
        const val MIME_TYPE = "text/plain"
        const val FILE_NAME = "auth.txt"

        fun parse(uriString: String): OtpSerializable {
            val uri = uriString.toOtpUri()
            val type = Auth.Type.valueOf(uri.type)
            val hash = uri.algorithm?.let(HOTP.Hash::valueOf)

            return when (type) {
                Auth.Type.HOTP -> HotpSerializable(
                    issuer = uri.issuer,
                    name = uri.name,
                    secret = uri.secret,
                    hash = hash ?: HOTP.Hash.SHA1,
                    digits = uri.digits ?: 6,
                    counter = uri.counter ?: 0,
                )

                Auth.Type.TOTP -> TotpSerializable(
                    issuer = uri.issuer,
                    name = uri.name,
                    secret = uri.secret,
                    hash = hash ?: HOTP.Hash.SHA1,
                    digits = uri.digits ?: 6,
                    period = uri.period ?: 30,
                )
            }
        }

        fun decodeFrom(input: InputStream): AuthTxt {
            val auths = mutableListOf<OtpSerializable>()
            for (uriString in input.bufferedReader().lines()) {
                runCatching {
                    parse(uriString)
                }.onSuccess {
                    auths.add(it)
                }
            }

            return AuthTxt(
                hotp = auths.filterIsInstance<HotpSerializable>(),
                totp = auths.filterIsInstance<TotpSerializable>()
            )
        }
    }
}