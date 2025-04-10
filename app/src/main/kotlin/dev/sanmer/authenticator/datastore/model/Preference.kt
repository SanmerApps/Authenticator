package dev.sanmer.authenticator.datastore.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class Preference(
    @ProtoNumber(1)
    val keyEncryptedByPassword: String = "",
    @ProtoNumber(2)
    val keyEncryptedByBiometric: String = "",
    @ProtoNumber(3)
    val ntpAddress: String = "",
    @ProtoNumber(4)
    val ntp: Ntp = Ntp.Cloudflare
) {
    val isEncrypted inline get() = keyEncryptedByPassword.isNotEmpty()
    val isBiometric inline get() = keyEncryptedByBiometric.isNotEmpty()

    fun encodeToStream(output: OutputStream) = output.write(
        ProtoBuf.encodeToByteArray(this)
    )

    companion object Default {
        fun decodeFromStream(input: InputStream): Preference =
            ProtoBuf.decodeFromByteArray(input.readBytes())
    }
}