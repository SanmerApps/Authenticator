package dev.sanmer.attestation

import dev.sanmer.ktx.asInt
import dev.sanmer.ktx.asSequence
import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.ASN1TaggedObject

class AuthorizationList private constructor() {
    internal constructor(values: ASN1Sequence) : this() {
        copy(values)
    }

    internal constructor(value: ASN1Encodable) : this(
        values = value.asSequence()
    )

    var rootOfTrust = RootOfTrust.EMPTY
        private set

    var osVersion = -1
        private set

    var osPatchLevel = -1
        private set

    internal fun copy(values: ASN1Sequence): AuthorizationList {
        for (it in values) {
            if (it !is ASN1TaggedObject) break
            val value = it.baseObject.toASN1Primitive()

            when (it.tagNo) {
                KM_TAG_ROOT_OF_TRUST and KEYMASTER_TAG_TYPE_MASK -> {
                    rootOfTrust = RootOfTrust(value)
                }

                KM_TAG_OS_VERSION and KEYMASTER_TAG_TYPE_MASK -> {
                    osVersion = value.asInt()
                }

                KM_TAG_OS_PATCHLEVEL and KEYMASTER_TAG_TYPE_MASK -> {
                    osPatchLevel = value.asInt()
                }

                else -> {}
            }
        }

        return this
    }

    internal companion object {
        private const val KM_UINT = 3 shl 28
        private const val KM_BYTES = 9 shl 28

        const val KM_TAG_ROOT_OF_TRUST = KM_BYTES or 704
        const val KM_TAG_OS_VERSION: Int = KM_UINT or 705
        const val KM_TAG_OS_PATCHLEVEL = KM_UINT or 706

        const val KEYMASTER_TAG_TYPE_MASK = 0x0FFFFFFF

        val EMPTY get() = AuthorizationList()
    }
}