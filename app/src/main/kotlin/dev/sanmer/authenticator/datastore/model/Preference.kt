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
    val ntpAddress: String = "",
    @ProtoNumber(2)
    val ntp: Ntp = Ntp.Apple
) {
    fun encodeToStream(output: OutputStream) = output.write(
        ProtoBuf.encodeToByteArray(this)
    )

    companion object Default {
        fun decodeFromStream(input: InputStream): Preference =
            ProtoBuf.decodeFromByteArray(input.readBytes())
    }
}