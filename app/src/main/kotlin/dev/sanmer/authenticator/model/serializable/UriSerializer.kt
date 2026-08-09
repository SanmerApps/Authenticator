package dev.sanmer.authenticator.model.serializable

import android.net.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object UriSerializer : KSerializer<Uri> {
    override val descriptor = SerialDescriptor(
        serialName = "android.net.Uri",
        original = serialDescriptor<String>()
    )

    override fun serialize(
        encoder: Encoder,
        value: Uri
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uri {
        return Uri.parse(decoder.decodeString())
    }
}