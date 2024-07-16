@file:Suppress("NOTHING_TO_INLINE")

package dev.sanmer.ktx

import org.bouncycastle.asn1.ASN1Boolean
import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Enumerated
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.ASN1OctetString
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.DEROctetString
import java.io.IOException
import java.security.cert.CertificateParsingException

private inline fun ByteArray.readASN1() = try {
    ASN1InputStream(this).use { it.readObject() }
} catch (e: IOException) {
    throw CertificateParsingException("Failed to parse ASN.1", e)
}

internal fun ByteArray.toASN1Sequence(): ASN1Sequence {
    val octets = when (val value = readASN1()) {
        is ASN1OctetString -> value.octets
        else -> throw CertificateParsingException(
            "Expected ASN1OctetString, found ${value.javaClass.name}"
        )
    }

    return when (val value = octets.readASN1()) {
        is ASN1Sequence -> value
        else -> throw CertificateParsingException(
            "Expected ASN1Sequence, found ${value.javaClass.name}"
        )
    }
}

internal inline fun ASN1Encodable.asSequence() = when (this) {
    is ASN1Sequence -> this
    else -> throw CertificateParsingException(
        "Expected ASN1Sequence"
    )
}

internal fun ASN1Encodable.asByteArray() = when (this) {
    is DEROctetString -> octets
    else -> throw CertificateParsingException("Expected DEROctetString")
}

internal fun ASN1Encodable.asBoolean() = when (this) {
    ASN1Boolean.TRUE -> true
    ASN1Boolean.FALSE -> false
    else -> throw CertificateParsingException(
        "DER-encoded boolean values must contain either 0x00 or 0xFF"
    )
}

internal fun ASN1Encodable.asInt() = when (this) {
    is ASN1Integer -> value.toInt()
    is ASN1Enumerated -> value.toInt()
    else -> throw CertificateParsingException(
        "Integer value expected, ${javaClass.name} found"
    )
}

internal inline fun ASN1Sequence.getObjectAtOrNull(index: Int) =
    if (index in 0 until size()) getObjectAt(index) else null