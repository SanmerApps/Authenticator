package dev.sanmer.authenticator.model.serializer

import java.io.OutputStream

interface AuthSerializable<T> {
    val auths: List<T>
    fun encodeTo(output: OutputStream)
}