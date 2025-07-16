package dev.sanmer.authenticator.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import dev.sanmer.authenticator.datastore.model.Preference
import kotlinx.serialization.SerializationException
import java.io.InputStream
import java.io.OutputStream

class PreferenceSerializer : Serializer<Preference> {
    override val defaultValue = Preference()

    override suspend fun readFrom(input: InputStream) =
        try {
            Preference.decodeFromStream(input)
        } catch (e: SerializationException) {
            throw CorruptionException("Failed to read proto", e)
        }

    override suspend fun writeTo(t: Preference, output: OutputStream) {
        t.encodeToStream(output)
    }
}